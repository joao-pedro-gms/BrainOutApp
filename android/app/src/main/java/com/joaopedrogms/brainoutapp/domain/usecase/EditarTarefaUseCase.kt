package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Edita uma tarefa existente.
 *
 * Mantém `id`, `projetoId`, `createdAt` originais e atualiza apenas os
 * campos editáveis (`titulo`, `descricao`, `prazo`, `status`,
 * `prioridade`, `responsavel`, `dependencias`). `updatedAt` recebe
 * `Instant.now()` (UTC) — usado pelo `ORDER BY updated_at DESC` da
 * lista.
 *
 * **RN03 (issue #11):** valida que `prazo` da tarefa (se preenchido)
 * seja `≤ prazo` do projeto. Se o projeto não tem prazo, qualquer
 * prazo é aceito. Lança [com.joaopedrogms.brainoutapp.domain.exception
 * .RegrasNegocioException] com mensagem amigável caso contrário —
 * mesma string usada por [CriarTarefaUseCase].
 *
 * **Mudança de projeto:** esta lane não suporta mover tarefa entre
 * projetos (continua responsabilidade de uma lane futura). O
 * `projetoId` da tarefa atual é o que vale para RN03.
 */
class EditarTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
    private val projetoRepository: ProjetoRepository,
) {
    /**
     * @param atual       tarefa vinda do repository (fonte de `id`/`projetoId`/`createdAt`).
     * @param titulo      novo título.
     * @param descricao   nova descrição (pode ser `null`).
     * @param prazo       novo prazo (pode ser `null`).
     * @param status      novo status.
     * @param prioridade  nova prioridade.
     * @param responsavel novo responsável (pode ser `null`).
     * @param dependencias novas dependências (default: preserva as atuais).
     * @throws com.joaopedrogms.brainoutapp.domain.exception.RegrasNegocioException
     *         se [prazo] ultrapassar o prazo do projeto (RN03).
     */
    suspend operator fun invoke(
        atual: Tarefa,
        titulo: String,
        descricao: String?,
        prazo: LocalDate?,
        status: StatusTarefa,
        prioridade: PrioridadeTarefa,
        responsavel: String?,
        dependencias: List<String> = atual.dependencias,
    ): Tarefa {
        validarPrazoContraProjeto(prazo, atual.projetoId, projetoRepository)

        val atualizada = atual.copy(
            titulo = titulo,
            descricao = descricao,
            prazo = prazo,
            status = status,
            prioridade = prioridade,
            responsavel = responsavel,
            dependencias = dependencias,
            updatedAt = Instant.now(),
        )
        repository.update(atualizada)
        return atualizada
    }
}
