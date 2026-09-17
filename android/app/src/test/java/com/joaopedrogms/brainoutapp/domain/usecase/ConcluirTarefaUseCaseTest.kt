package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.makeTarefa
import com.joaopedrogms.brainoutapp.fakes.FakeTarefaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes JUnit 4 do [ConcluirTarefaUseCase] — RN01.
 *
 * Cobertura exigida (issue #11):
 *  - ✅ Tarefa **sem** dependências pode ser concluída.
 *  - ✅ Tarefa com dependência **aberta** NÃO pode ser concluída (lança
 *    [RegrasNegocioException] com mensagem amigável).
 *  - ✅ Tarefa com dependência **concluída** PODE ser concluída.
 *
 * Bônus desta lane (não exigido pelo brief, mas barato de testar):
 *  - Idempotência: chamar `concluir` em tarefa já concluída é no-op.
 *  - Tarefa inexistente lança [RegrasNegocioException].
 *  - Dependência soft-deleted é tratada como satisfeita.
 *  - Múltiplas dependências — basta uma aberta para bloquear.
 */
class ConcluirTarefaUseCaseTest {

    private lateinit var repo: FakeTarefaRepository
    private lateinit var useCase: ConcluirTarefaUseCase

    @Before
    fun setUp() {
        repo = FakeTarefaRepository()
        useCase = ConcluirTarefaUseCase(repo)
    }

    @Test
    fun `tarefa sem dependencias pode ser concluida`() = runTest {
        // Arrange
        val tarefa = repo.salvar(makeTarefa(id = "t1", titulo = "Única"))

        // Act
        val resultado = useCase("t1")

        // Assert
        assertEquals(StatusTarefa.CONCLUIDA, resultado.status)
        // Garante que o repo foi atualizado (não só retornou um objeto novo).
        assertEquals(StatusTarefa.CONCLUIDA, repo.getByIdOnce("t1")?.status)
    }

    @Test
    fun `tarefa com dependencia em aberto NAO pode ser concluida`() = runTest {
        // Arrange: tarefa principal depende de "dep1", que está ABERTA.
        repo.salvar(makeTarefa(id = "dep1", titulo = "Dependência Pendente", status = StatusTarefa.ABERTA))
        repo.salvar(
            makeTarefa(
                id = "principal",
                titulo = "Tarefa Alvo",
                dependencias = listOf("dep1"),
            ),
        )

        // Act + Assert: dentro de `runTest` podemos chamar suspends
        // diretamente; capturamos via try/catch para inspecionar a mensagem.
        val ex = try {
            useCase("principal")
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        assertTrue(
            "esperava RegrasNegocioException referenciando a tarefa pendente",
            ex?.message?.contains("Dependência Pendente") == true,
        )
        // Repo não foi tocado.
        assertEquals(StatusTarefa.ABERTA, repo.getByIdOnce("principal")?.status)
    }

    @Test
    fun `tarefa com dependencia concluida PODE ser concluida`() = runTest {
        // Arrange: dependência já CONCLUIDA — caminho feliz.
        repo.salvar(
            makeTarefa(
                id = "dep1",
                titulo = "Dependência OK",
                status = StatusTarefa.CONCLUIDA,
            ),
        )
        repo.salvar(
            makeTarefa(
                id = "principal",
                titulo = "Tarefa Alvo",
                dependencias = listOf("dep1"),
            ),
        )

        // Act
        val resultado = useCase("principal")

        // Assert
        assertEquals(StatusTarefa.CONCLUIDA, resultado.status)
    }

    @Test
    fun `dependencia em_andamento tambem bloqueia a conclusao`() = runTest {
        // Arrange: EM_ANDAMENTO é "aberta" pela definição de RN02
        // (`ABERTA` ou `EM_ANDAMENTO`) — manter consistência entre as
        // regras. Aqui validamos explicitamente o caso.
        repo.salvar(
            makeTarefa(
                id = "dep1",
                titulo = "Em andamento",
                status = StatusTarefa.EM_ANDAMENTO,
            ),
        )
        repo.salvar(makeTarefa(id = "principal", dependencias = listOf("dep1")))

        // Act + Assert
        try {
            useCase("principal")
            org.junit.Assert.fail("esperava RegrasNegocioException")
        } catch (e: RegrasNegocioException) {
            // ok
        }
    }

    @Test
    fun `multiplas dependencias - basta uma aberta para bloquear`() = runTest {
        // Arrange: 3 deps, 2 ok e 1 aberta.
        repo.salvar(makeTarefa(id = "d1", titulo = "OK", status = StatusTarefa.CONCLUIDA))
        repo.salvar(makeTarefa(id = "d2", titulo = "OK", status = StatusTarefa.CONCLUIDA))
        repo.salvar(makeTarefa(id = "d3", titulo = "Bloqueio", status = StatusTarefa.ABERTA))
        repo.salvar(
            makeTarefa(
                id = "principal",
                dependencias = listOf("d1", "d2", "d3"),
            ),
        )

        // Act + Assert
        val ex = try {
            useCase("principal")
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        assertTrue(ex?.message?.contains("Bloqueio") == true)
    }

    @Test
    fun `concluir tarefa ja concluida e idempotente`() = runTest {
        // Arrange
        repo.salvar(
            makeTarefa(
                id = "t1",
                titulo = "Já pronta",
                status = StatusTarefa.CONCLUIDA,
            ),
        )

        // Act — chamar de novo não deve jogar nem mudar updatedAt.
        val antes = repo.getByIdOnce("t1")!!
        val depois = useCase("t1")

        // Assert
        assertEquals(StatusTarefa.CONCLUIDA, depois.status)
        // updatedAt preservado (idempotente).
        assertEquals(antes.updatedAt, depois.updatedAt)
    }

    @Test
    fun `tarefa inexistente lanca RegrasNegocioException`() = runTest {
        try {
            useCase("fantasma")
            org.junit.Assert.fail("esperava RegrasNegocioException")
        } catch (e: RegrasNegocioException) {
            assertNotEquals(null, e.message)
        }
    }

    @Test
    fun `dependencia soft-deletada e tratada como satisfeita`() = runTest {
        // Arrange: a dependência foi removida (soft delete no impl real
        // → remoção direta no fake). Conclusão deve prosseguir.
        repo.salvar(makeTarefa(id = "dep1", titulo = "Apagada", status = StatusTarefa.ABERTA))
        repo.softDelete("dep1")
        repo.salvar(makeTarefa(id = "principal", dependencias = listOf("dep1")))

        // Act
        val resultado = useCase("principal")

        // Assert
        assertEquals(StatusTarefa.CONCLUIDA, resultado.status)
    }
}
