package com.joaopedrogms.brainoutapp.domain.usecase

import com.joaopedrogms.brainoutapp.data.local.UuidV7
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Cria uma nova tarefa.
 *
 * **Esta lane (#10)** só persiste e gera id/timestamps. Validações de
 * `titulo ≤ 100`, `descricao ≤ 500`, `prazo ≥ hoje` moram no
 * `TarefaFormViewModel`. Regras RN01-RN03 (incluindo prazo ≤ prazo do
 * projeto) entram no ciclo 3.
 *
 * Padrões aplicados:
 *  - `status` inicial = [StatusTarefa.ABERTA].
 *  - `prioridade` inicial = [PrioridadeTarefa.BAIXA] (sobrescrevível pela UI).
 */
class CriarTarefaUseCase @Inject constructor(
    private val repository: TarefaRepository,
) {
    /**
     * @param projetoId   FK para o projeto pai (obrigatório, validado na UI).
     * @param titulo      título (validado pela UI).
     * @param descricao   descrição opcional.
     * @param prazo       prazo opcional; `null` quando não informado.
     * @param prioridade  prioridade; default [PrioridadeTarefa.BAIXA].
     * @param responsavel nome do responsável (opcional).
     * @return a [Tarefa] recém-criada.
     */
    suspend operator fun invoke(
        projetoId: String,
        titulo: String,
        descricao: String?,
        prazo: LocalDate?,
        prioridade: PrioridadeTarefa = PrioridadeTarefa.BAIXA,
        responsavel: String? = null,
    ): Tarefa {
        val agora = Instant.now()
        val tarefa = Tarefa(
            id = UuidV7.novo(),
            projetoId = projetoId,
            titulo = titulo,
            descricao = descricao,
            prazo = prazo,
            status = StatusTarefa.ABERTA,
            prioridade = prioridade,
            responsavel = responsavel,
            createdAt = agora,
            updatedAt = agora,
        )
        repository.insert(tarefa)
        return tarefa
    }
}
