package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Edita um projeto existente.
 *
 * Mantém `id`, `createdAt` originais e atualiza apenas os campos editáveis
 * (`nome`, `descricao`, `prazo`, `status`). `updatedAt` recebe `Instant.now()`
 * (UTC) — usado pelo `ORDER BY updated_at DESC` da lista.
 *
 * Validações de input continuam na UI (form); regras RN01-03 no ciclo 3.
 */
class EditarProjetoUseCase @Inject constructor(
    private val repository: ProjetoRepository,
) {
    /**
     * @param atual     projeto vindo do repository (fonte de `id`/`createdAt`).
     * @param nome      novo nome.
     * @param descricao nova descrição (pode ser `null`).
     * @param prazo     novo prazo (pode ser `null`).
     * @param status    novo status. Mantém [StatusProjeto.ABERTO] quando a UI
     *                  não permite editar (ciclo 3 pode liberar alteração).
     */
    suspend operator fun invoke(
        atual: Projeto,
        nome: String,
        descricao: String?,
        prazo: LocalDate?,
        status: StatusProjeto,
    ): Projeto {
        val atualizado = atual.copy(
            nome = nome,
            descricao = descricao,
            prazo = prazo,
            status = status,
            updatedAt = Instant.now(),
        )
        repository.update(atualizado)
        return atualizado
    }
}