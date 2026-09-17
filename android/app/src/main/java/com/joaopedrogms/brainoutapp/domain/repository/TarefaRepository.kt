package com.joaopedrogms.brainoutapp.domain.repository

import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de domínio para persistência de tarefas.
 *
 * Implementação fica em `data/repository/TarefaRepositoryImpl.kt`
 * (ligada via Hilt). Use cases e ViewModels consomem **apenas** esta
 * interface — nada de Room ou detalhes de storage vaza para `domain/`.
 *
 * Regras RN01-RN03 (issue #11) moram nos use cases; aqui só
 * disponibilizamos as leituras pontuais (snapshot) necessárias:
 *  - [getByIdsOnce]   — carregar dependências (RN01) sem depender de `Flow`.
 *  - [getByProjetoOnce] — contar tarefas em aberto de um projeto (RN02).
 * A leitura `Flow` paralela ([getByProjeto]) continua para a UI.
 */
interface TarefaRepository {

    /** Lista todas as tarefas ativas, reativa (emite a cada insert/update/softDelete). */
    fun getAll(): Flow<List<Tarefa>>

    /** Lista as tarefas de um projeto específico (ativas), reativa. */
    fun getByProjeto(projetoId: String): Flow<List<Tarefa>>

    /** Detalhe de uma tarefa pelo id, reativo. `null` se não existir / soft-deleted. */
    fun getById(id: String): Flow<Tarefa?>

    /** Snapshot único (não-Flow) para leituras pontuais (RN01, form no carregamento inicial). */
    suspend fun getByIdOnce(id: String): Tarefa?

    /**
     * Snapshot único das tarefas de um projeto (ativas). Usado por
     * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirProjetoUseCase]
     * para contar tarefas em aberto (RN02) sem manter um `Flow` ativo.
     *
     * > Retorna **todas** as tarefas ativas (não só as abertas) — quem
     * > chama decide o predicado de "aberta".
     */
    suspend fun getByProjetoOnce(projetoId: String): List<Tarefa>

    /**
     * Snapshot único de várias tarefas por id (ativas). Usado por
     * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirTarefaUseCase]
     * (RN01). Soft-deleted são filtradas — dependência removida conta
     * como satisfeita. Ids inexistentes são ignorados silenciosamente.
     */
    suspend fun getByIdsOnce(ids: List<String>): List<Tarefa>

    /** Persiste uma tarefa nova. `id`/`createdAt`/`updatedAt` são de responsabilidade do caller. */
    suspend fun insert(tarefa: Tarefa)

    /** Atualiza uma tarefa existente (mesmo `id`). */
    suspend fun update(tarefa: Tarefa)

    /** Soft delete: marca `deleted_at` sem remover a linha (preserva auditoria local). */
    suspend fun delete(id: String)
}
