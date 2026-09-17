package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Lista todas as tarefas ativas (soft-deleted filtradas no DAO).
 *
 * Wrapper sobre [TarefaRepository.getAll] — usado pelo
 * `TarefaListViewModel`. Mantido como use case para consistência com o
 * guideline R12 (UI nunca acessa repository direto).
 */
class ListarTarefasUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    operator fun invoke(): Flow<List<Tarefa>> = repository.getAll()
}
