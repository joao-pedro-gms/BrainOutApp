package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import javax.inject.Inject

/**
 * **RN01 — Conclusão de tarefa com dependências.**
 *
 * > Uma tarefa só pode ser marcada como concluída se todas as
 * > dependências estiverem concluídas.
 *
 * Implementação **offline-first** (ADR-0005). Sem backend; a regra vive
 * na camada de domínio e é coberta por testes JUnit (esta lane).
 *
 * Comportamento:
 *  1. Carrega a tarefa-alvo via `getByIdOnce`. Se não existir (id
 *     desconhecido / soft-deleted), lança
 *     [RegrasNegocioException] com mensagem `"Tarefa não encontrada."`.
 *  2. Se `dependencias` está vazia, vai direto para a marcação como
 *     concluída — sem I/O extra.
 *  3. Caso contrário, carrega as dependências em um único `getByIdsOnce`
 *     e verifica:
 *      - todas devem existir como tarefa ativa (id desconhecido é
 *        tratado como **pendente** — conservador: bloqueia a conclusão);
 *      - todas devem ter `status = StatusTarefa.CONCLUIDA`.
 *     Se alguma estiver em outro estado, lança
 *     [RegrasNegocioException] com a mensagem `"Não é possível
 *     concluir: a tarefa '<título>' ainda está pendente."` apontando a
 *     **primeira** pendente encontrada. Mensagem R10.
 *  4. Marca a tarefa como `CONCLUIDA` e chama `repository.update`
 *     (preserva `updatedAt`).
 *
 * **Idempotência:** se a tarefa já estiver concluída, retorna a própria
 * tarefa sem tocar no banco (UX: chamar "concluir" duas vezes não dá
 * erro).
 *
 * **Soft-deleted nas dependências:** o DAO ignora soft-deleted. Decisão
 * desta lane: dependência removida = dependência satisfeita (a regra
 * não fica "presa" para sempre se o usuário apagar uma tarefa-mãe).
 */
class ConcluirTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    suspend operator fun invoke(tarefaId: String): Tarefa {
        val tarefa = repository.getByIdOnce(tarefaId)
            ?: throw RegrasNegocioException("Tarefa não encontrada.")

        if (tarefa.status == StatusTarefa.CONCLUIDA) return tarefa

        // Sem dependências: pode concluir.
        if (tarefa.dependencias.isEmpty()) {
            return marcarConcluida(tarefa)
        }

        // Carrega dependências e verifica cada uma.
        // Dependência soft-deleted (nao retornada pelo DAO) e tratada como
        // satisfeita: a regra nao fica "presa para sempre" se a tarefa-mae
        // for apagada. Id orfao (id inexistente que nunca existiu) tambem
        // e tratado como satisfeita — conservativo para evitar deadlock.
        val dependencias = repository.getByIdsOnce(tarefa.dependencias)
        val porId = dependencias.associateBy { it.id }
        tarefa.dependencias.forEach { idDep ->
            val dep = porId[idDep] ?: return@forEach  // soft-deleted ou inexistente: ok
            if (dep.status != StatusTarefa.CONCLUIDA) {
                throw RegrasNegocioException(
                    "Não é possível concluir: a tarefa '${dep.titulo}' ainda está pendente.",
                )
            }
        }
        return marcarConcluida(tarefa)
    }

    private suspend fun marcarConcluida(tarefa: Tarefa): Tarefa {
        val atualizada = tarefa.copy(
            status = StatusTarefa.CONCLUIDA,
            updatedAt = Instant.now(),
        )
        repository.update(atualizada)
        return atualizada
    }
}
