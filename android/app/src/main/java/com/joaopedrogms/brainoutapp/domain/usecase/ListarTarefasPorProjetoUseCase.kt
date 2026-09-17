package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Lista tarefas de um projeto específico (ativas).
 *
 * Usado pelo detalhe do projeto — quando o usuário abre um projeto,
 * a tela mostra as tarefas filhas. Esta lane já expõe o use case; a
 * tela de detalhes que consome é responsabilidade de outra lane
 * (a lane de Projetos não lê tarefas ainda).
 */
class ListarTarefasPorProjetoUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    operator fun invoke(projetoId: String): Flow<List<Tarefa>> =
        repository.getByProjeto(projetoId)
}
