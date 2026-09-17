package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.makeProjeto
import com.joaopedrogms.brainoutapp.domain.model.makeTarefa
import com.joaopedrogms.brainoutapp.fakes.FakeProjetoRepository
import com.joaopedrogms.brainoutapp.fakes.FakeTarefaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes JUnit 4 do [ConcluirProjetoUseCase] — RN02.
 *
 * Cobertura exigida (issue #11):
 *  - ✅ Projeto **sem tarefas** pode ser concluído.
 *  - ✅ Projeto com **1 tarefa aberta** NÃO pode ser concluído
 *    (mensagem R10: `"Projeto tem 1 tarefa(s) em aberto. Conclua ou
 *    cancele antes."`).
 *  - ✅ Projeto com **1 tarefa concluída** PODE ser concluído.
 *
 * Bônus desta lane:
 *  - Idempotência.
 *  - Tarefas `CANCELADA` **não** bloqueiam (cancelar é decisão válida).
 *  - Mensagem cita a contagem correta quando há várias abertas.
 *  - Projeto inexistente lança [RegrasNegocioException].
 */
class ConcluirProjetoUseCaseTest {

    private lateinit var projetoRepo: FakeProjetoRepository
    private lateinit var tarefaRepo: FakeTarefaRepository
    private lateinit var useCase: ConcluirProjetoUseCase

    @Before
    fun setUp() {
        projetoRepo = FakeProjetoRepository()
        tarefaRepo = FakeTarefaRepository()
        useCase = ConcluirProjetoUseCase(projetoRepo, tarefaRepo)
    }

    @Test
    fun `projeto sem tarefas pode ser concluido`() = runTest {
        // Arrange
        projetoRepo.salvar(makeProjeto(id = "p1", nome = "Vazio"))

        // Act
        val resultado = useCase("p1")

        // Assert
        assertEquals(StatusProjeto.CONCLUIDO, resultado.status)
        assertEquals(StatusProjeto.CONCLUIDO, projetoRepo.getByIdOnce("p1")?.status)
    }

    @Test
    fun `projeto com uma tarefa aberta NAO pode ser concluido`() = runTest {
        // Arrange: projeto + 1 tarefa em aberto.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        tarefaRepo.salvar(
            makeTarefa(
                id = "t1",
                projetoId = "p1",
                titulo = "Aberta",
                status = StatusTarefa.ABERTA,
            ),
        )

        // Act + Assert
        val ex = try {
            useCase("p1")
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        assertTrue(
            "mensagem deve citar a contagem: ${ex?.message}",
            ex?.message?.contains("1 tarefa(s)") == true,
        )
        assertTrue(ex?.message?.contains("em aberto") == true)
        // Projeto não foi tocado.
        assertEquals(StatusProjeto.ABERTO, projetoRepo.getByIdOnce("p1")?.status)
    }

    @Test
    fun `projeto com tarefa concluida PODE ser concluido`() = runTest {
        // Arrange: a tarefa do projeto já está concluída — caminho feliz.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        tarefaRepo.salvar(
            makeTarefa(
                id = "t1",
                projetoId = "p1",
                status = StatusTarefa.CONCLUIDA,
            ),
        )

        // Act
        val resultado = useCase("p1")

        // Assert
        assertEquals(StatusProjeto.CONCLUIDO, resultado.status)
    }

    @Test
    fun `tarefa em_andamento tambem bloqueia a conclusao do projeto`() = runTest {
        // Arrange — mesma regra da RN01: "aberta" inclui EM_ANDAMENTO.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        tarefaRepo.salvar(
            makeTarefa(
                id = "t1",
                projetoId = "p1",
                status = StatusTarefa.EM_ANDAMENTO,
            ),
        )

        // Act + Assert
        val ex = try {
            useCase("p1")
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        assertTrue(ex?.message?.contains("1 tarefa(s)") == true)
    }

    @Test
    fun `tarefa cancelada NAO bloqueia a conclusao do projeto`() = runTest {
        // Arrange: cancelar é decisão válida — não bloqueia.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        tarefaRepo.salvar(
            makeTarefa(
                id = "t1",
                projetoId = "p1",
                status = StatusTarefa.CANCELADA,
            ),
        )

        // Act
        val resultado = useCase("p1")

        // Assert
        assertEquals(StatusProjeto.CONCLUIDO, resultado.status)
    }

    @Test
    fun `contagem na mensagem bate com o numero real de abertas`() = runTest {
        // Arrange: 3 abertas + 1 concluída + 1 cancelada → 3 pendentes.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        repeat(3) { i ->
            tarefaRepo.salvar(
                makeTarefa(
                    id = "ab-$i",
                    projetoId = "p1",
                    status = StatusTarefa.ABERTA,
                ),
            )
        }
        tarefaRepo.salvar(
            makeTarefa(
                id = "ok",
                projetoId = "p1",
                status = StatusTarefa.CONCLUIDA,
            ),
        )
        tarefaRepo.salvar(
            makeTarefa(
                id = "canc",
                projetoId = "p1",
                status = StatusTarefa.CANCELADA,
            ),
        )

        // Act + Assert
        val ex = try {
            useCase("p1")
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        assertTrue(
            "esperava '3 tarefa(s)' na mensagem: ${ex?.message}",
            ex?.message?.contains("3 tarefa(s)") == true,
        )
    }

    @Test
    fun `concluir projeto ja concluido e idempotente`() = runTest {
        // Arrange
        projetoRepo.salvar(
            makeProjeto(
                id = "p1",
                status = StatusProjeto.CONCLUIDO,
            ),
        )

        // Act — chamar de novo não deve jogar nem mexer em `updatedAt`.
        val antes = projetoRepo.getByIdOnce("p1")!!
        val depois = useCase("p1")

        // Assert
        assertEquals(StatusProjeto.CONCLUIDO, depois.status)
        assertEquals(antes.updatedAt, depois.updatedAt)
    }

    @Test
    fun `projeto inexistente lanca RegrasNegocioException`() = runTest {
        try {
            useCase("fantasma")
            org.junit.Assert.fail("esperava RegrasNegocioException")
        } catch (e: RegrasNegocioException) {
            // ok
        }
    }

    @Test
    fun `tarefas de outro projeto nao bloqueiam este`() = runTest {
        // Arrange: tarefa aberta em outro projeto não pode barrar.
        projetoRepo.salvar(makeProjeto(id = "p1"))
        projetoRepo.salvar(makeProjeto(id = "p2"))
        tarefaRepo.salvarTarefaDeOutroProjeto(
            makeTarefa(
                id = "outra",
                projetoId = "p2",
                status = StatusTarefa.ABERTA,
            ),
        )

        // Act
        val resultado = useCase("p1")

        // Assert
        assertEquals(StatusProjeto.CONCLUIDO, resultado.status)
    }
}

/**
 * Helper privado: o `FakeTarefaRepository.salvar` já cobre outros
 * projetos — mas o teste acima quer explicitar a invariante
 * "isolamento por projeto". O helper só dá nome semântico; a
 * implementação é a mesma.
 */
private fun FakeTarefaRepository.salvarTarefaDeOutroProjeto(t: com.joaopedrogms.brainoutapp.domain.model.Tarefa) =
    this.salvar(t)
