package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Edita uma tarefa existente.
 *
 * Mantém `id`, `projetoId`, `createdAt` originais e atualiza apenas os
 * campos editáveis (`titulo`, `descricao`, `prazo`, `status`,
 * `prioridade`, `responsavel`). `updatedAt` recebe `Instant.now()` (UTC)
 * — usado pelo `ORDER BY updated_at DESC` da lista.
 *
 * Validações de input continuam na UI (form); regras RN01-03 no ciclo 3.
 */
class EditarTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    /**
     * @param atual       tarefa vinda do repository (fonte de `id`/`projetoId`/`createdAt`).
     * @param titulo      novo título.
     * @param descricao   nova descrição (pode ser `null`).
     * @param prazo       novo prazo (pode ser `null`).
     * @param status      novo status.
     * @param prioridade  nova prioridade.
     * @param responsavel novo responsável (pode ser `null`).
     */
    suspend operator fun invoke(
        atual: Tarefa,
        titulo: String,
        descricao: String?,
        prazo: LocalDate?,
        status: StatusTarefa,
        prioridade: PrioridadeTarefa,
        responsavel: String?,
    ): Tarefa {
        val atualizada = atual.copy(
            titulo = titulo,
            descricao = descricao,
            prazo = prazo,
            status = status,
            prioridade = prioridade,
            responsavel = responsavel,
            updatedAt = Instant.now(),
        )
        repository.update(atualizada)
        return atualizada
    }
}
