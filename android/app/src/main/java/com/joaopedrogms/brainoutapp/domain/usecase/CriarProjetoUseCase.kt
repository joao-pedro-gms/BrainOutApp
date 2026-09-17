package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.data.local.UuidV7
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Cria um novo projeto.
 *
 * **Esta lane (#8)** só persiste e gera id/timestamps. Validações de
 * `nome ≤ 100`, `descricao ≤ 500`, `prazo ≥ hoje` moram no
 * `ProjetoFormViewModel` (responsabilidade de feedback de UI).
 * Regras de negócio formais (RN01-RN03) entram no ciclo 3.
 */
class CriarProjetoUseCase @Inject constructor(
    private val repository: ProjetoRepository,
) {
    /**
     * @param nome      nome do projeto (validado pela UI).
     * @param descricao descrição opcional.
     * @param prazo     prazo opcional; `null` quando não informado.
     * @return o [Projeto] recém-criado (com `id`, `createdAt`, `updatedAt`).
     */
    suspend operator fun invoke(
        nome: String,
        descricao: String?,
        prazo: java.time.LocalDate?,
    ): Projeto {
        val agora = Instant.now()
        val projeto = Projeto(
            id = UuidV7.novo(),
            nome = nome,
            descricao = descricao,
            prazo = prazo,
            status = StatusProjeto.ABERTO,
            createdAt = agora,
            updatedAt = agora,
        )
        repository.insert(projeto)
        return projeto
    }
}