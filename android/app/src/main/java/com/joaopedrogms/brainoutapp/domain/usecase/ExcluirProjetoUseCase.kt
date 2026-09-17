package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import javax.inject.Inject

/**
 * Exclui (soft delete) um projeto.
 *
 * A RN02 ("Projeto não pode ser concluído enquanto houver tarefa aberta")
 * é responsabilidade do ciclo 3, mas a UI **não** deve chamar este use case
 * se o status for `CONCLUIDO` com tarefas em aberto. Por ora, ele apenas
 * delega ao repository; o ciclo 3 injeta a checagem.
 */
class ExcluirProjetoUseCase @Inject constructor(
    private val repository: ProjetoRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.delete(id)
    }
}