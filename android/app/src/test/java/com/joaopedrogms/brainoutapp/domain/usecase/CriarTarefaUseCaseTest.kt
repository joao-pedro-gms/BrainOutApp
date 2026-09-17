package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.makeProjeto
import com.joaopedrogms.brainoutapp.fakes.FakeProjetoRepository
import com.joaopedrogms.brainoutapp.fakes.FakeTarefaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Testes JUnit 4 do [CriarTarefaUseCase] estendido com **RN03**.
 *
 * Cobertura exigida (issue #11):
 *  - ✅ Prazo da tarefa **≤** prazo do projeto → OK.
 *  - ✅ Prazo da tarefa **>** prazo do projeto → lança
 *    [RegrasNegocioException] com mensagem `"Prazo da tarefa (DD/MM)
 *    ultrapassa o prazo do projeto (DD/MM)."`.
 *  - ✅ Projeto sem prazo, tarefa com qualquer prazo → OK.
 *
 * Bônus desta lane:
 *  - Tarefa sem prazo é sempre aceita (regra só se aplica se houver
 *    prazo na tarefa).
 *  - Projeto inexistente (id órfão) — regra é pulada (FK do SQLite
 *    joga depois; a regra semântica não tem o que validar).
 *  - Mensagem usa o formato `DD/MM` (locale pt-BR).
 *  - Dependências são preservadas na tarefa criada (RN01).
 */
class CriarTarefaUseCaseTest {

    private lateinit var projetoRepo: FakeProjetoRepository
    private lateinit var tarefaRepo: FakeTarefaRepository
    private lateinit var useCase: CriarTarefaUseCase

    @Before
    fun setUp() {
        projetoRepo = FakeProjetoRepository()
        tarefaRepo = FakeTarefaRepository()
        useCase = CriarTarefaUseCase(tarefaRepo, projetoRepo)
    }

    @Test
    fun `prazo da tarefa menor que prazo do projeto e aceito`() = runTest {
        // Arrange: projeto vence 30/06/2024, tarefa vence 15/06/2024.
        projetoRepo.salvar(
            makeProjeto(
                id = "p1",
                prazo = LocalDate.of(2024, 6, 30),
            ),
        )

        // Act
        val resultado = useCase(
            projetoId = "p1",
            titulo = "Tarefa no prazo",
            descricao = null,
            prazo = LocalDate.of(2024, 6, 15),
        )

        // Assert
        assertEquals(LocalDate.of(2024, 6, 15), resultado.prazo)
        assertNotNull(tarefaRepo.getByIdOnce(resultado.id))
    }

    @Test
    fun `prazo da tarefa igual ao prazo do projeto e aceito (limite inclusivo)`() = runTest {
        // Arrange: tarefa no mesmo dia do prazo do projeto.
        val mesmoDia = LocalDate.of(2024, 6, 30)
        projetoRepo.salvar(makeProjeto(id = "p1", prazo = mesmoDia))

        // Act
        val resultado = useCase(
            projetoId = "p1",
            titulo = "Mesmo dia",
            descricao = null,
            prazo = mesmoDia,
        )

        // Assert
        assertEquals(mesmoDia, resultado.prazo)
    }

    @Test
    fun `prazo da tarefa maior que prazo do projeto lanca RegrasNegocioException`() = runTest {
        // Arrange: projeto 30/06, tarefa 15/07 → viola.
        projetoRepo.salvar(
            makeProjeto(
                id = "p1",
                prazo = LocalDate.of(2024, 6, 30),
            ),
        )

        // Act + Assert
        val ex = try {
            useCase(
                projetoId = "p1",
                titulo = "Atrasada",
                descricao = null,
                prazo = LocalDate.of(2024, 7, 15),
            )
            null
        } catch (e: RegrasNegocioException) {
            e
        }
        // Verifica mensagem R10 (formato DD/MM).
        assertTrue(
            "mensagem deve conter 'Prazo da tarefa (15/07)': ${ex?.message}",
            ex?.message?.contains("Prazo da tarefa (15/07)") == true,
        )
        assertTrue(
            "mensagem deve conter 'prazo do projeto (30/06)': ${ex?.message}",
            ex?.message?.contains("prazo do projeto (30/06)") == true,
        )
        // Garante que nada foi persistido.
        assertEquals(0, tarefaRepo.getAllOnce().size)
    }

    @Test
    fun `projeto sem prazo - tarefa com qualquer prazo e aceita`() = runTest {
        // Arrange: projeto sem prazo.
        projetoRepo.salvar(makeProjeto(id = "p1", prazo = null))

        // Act — prazo bem distante no futuro.
        val resultado = useCase(
            projetoId = "p1",
            titulo = "Longa",
            descricao = null,
            prazo = LocalDate.of(2099, 12, 31),
        )

        // Assert
        assertEquals(LocalDate.of(2099, 12, 31), resultado.prazo)
    }

    @Test
    fun `tarefa sem prazo e sempre aceita independente do projeto`() = runTest {
        // Arrange: projeto com prazo curto, tarefa sem prazo.
        projetoRepo.salvar(
            makeProjeto(
                id = "p1",
                prazo = LocalDate.of(2024, 6, 30),
            ),
        )

        // Act
        val resultado = useCase(
            projetoId = "p1",
            titulo = "Sem prazo",
            descricao = null,
            prazo = null,
        )

        // Assert
        assertEquals(null, resultado.prazo)
    }

    @Test
    fun `projeto inexistente - regra e pulada (FK cuida depois)`() = runTest {
        // Arrange: nenhum projeto salvo.

        // Act
        val resultado = useCase(
            projetoId = "orfão",
            titulo = "Sem pai",
            descricao = null,
            prazo = LocalDate.of(2099, 1, 1),
        )

        // Assert: tarefa é criada — a regra semântica não tem como
        // validar (não há projeto), e a FK do SQLite vai barrar no
        // `repository.insert` real (no fake, `salvar` aceita tudo).
        assertEquals("orfão", resultado.projetoId)
    }

    @Test
    fun `dependencias informadas sao preservadas na tarefa criada`() = runTest {
        // Arrange
        projetoRepo.salvar(makeProjeto(id = "p1"))

        // Act
        val resultado = useCase(
            projetoId = "p1",
            titulo = "Com deps",
            descricao = null,
            prazo = null,
            dependencias = listOf("dep1", "dep2"),
        )

        // Assert
        assertEquals(listOf("dep1", "dep2"), resultado.dependencias)
    }
}
