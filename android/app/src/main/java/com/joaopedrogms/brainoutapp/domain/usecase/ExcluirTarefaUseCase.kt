package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import javax.inject.Inject

/**
 * Exclui (soft delete) uma tarefa.
 *
 * A RN01 (dependência entre tarefas — não permitir concluir com
 * dependência aberta) **não** é responsabilidade deste use case; é
 * uma checagem no `ConcluirTarefaUseCase` da lane de regras de
 * negócio. Aqui apenas delegamos ao repository.
 */
class ExcluirTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}
