package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import javax.inject.Inject

/**
 * **RN02 — Conclusão de projeto.**
 *
 * > Projeto não pode ser concluído enquanto houver tarefa aberta.
 *
 * Implementação **offline-first** (ADR-0005). Sem backend; a regra vive
 * na camada de domínio e é coberta por testes JUnit (esta lane).
 *
 * Comportamento:
 *  1. Carrega o projeto via `ProjetoRepository.getByIdOnce`. Se não
 *     existir, lança [RegrasNegocioException] `"Projeto não encontrado."`.
 *  2. Lista tarefas do projeto via `TarefaRepository.getByProjetoOnce`.
 *  3. Conta quantas estão em status `ABERTA` ou `EM_ANDAMENTO` (RN02
 *     explícita: "tarefa aberta" inclui as duas). `CONCLUIDA` e
 *     `CANCELADA` **não** bloqueiam — cancelar é decisão válida do
 *     usuário.
 *  4. Se `N > 0`, lança [RegrasNegocioException] com a mensagem
 *     `"Projeto tem N tarefa(s) em aberto. Conclua ou cancele antes."`
 *     (R10). O número de tarefas pendentes fica explícito na mensagem
 *     — ajuda o usuário a saber o que falta.
 *  5. Marca o projeto como `CONCLUIDO` via `repository.update`.
 *
 * **Idempotência:** se já estiver concluído, retorna o projeto sem
 * tocar no banco.
 */
class ConcluirProjetoUseCase @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val tarefaRepository: TarefaRepository,
) {
    suspend operator fun invoke(projetoId: String): Projeto {
        val projeto = projetoRepository.getByIdOnce(projetoId)
            ?: throw RegrasNegocioException("Projeto não encontrado.")

        if (projeto.status == StatusProjeto.CONCLUIDO) return projeto

        val tarefas = tarefaRepository.getByProjetoOnce(projetoId)
        val abertas = tarefas.count { it.status == StatusTarefa.ABERTA || it.status == StatusTarefa.EM_ANDAMENTO }
        if (abertas > 0) {
            throw RegrasNegocioException(
                "Projeto tem $abertas tarefa(s) em aberto. Conclua ou cancele antes.",
            )
        }

        val atualizado = projeto.copy(
            status = StatusProjeto.CONCLUIDO,
            updatedAt = Instant.now(),
        )
        projetoRepository.update(atualizado)
        return atualizado
    }
}
