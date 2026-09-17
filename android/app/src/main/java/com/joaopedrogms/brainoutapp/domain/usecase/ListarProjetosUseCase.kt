package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Lista todos os projetos ativos (soft-deleted filtrados no DAO).
 *
 * Wrapper sobre [ProjetoRepository.getAll] — usado pelo
 * `ProjetoListViewModel`. Mantido como use case para consistência com o
 * guideline R12 (UI nunca acessa repository direto).
 */
class ListarProjetosUseCase @Inject constructor(
    private val repository: ProjetoRepository,
) {
    operator fun invoke(): Flow<List<Projeto>> = repository.getAll()
}