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
 * Sem regra de negócio aqui: validações de RN01-RN03 serão injetadas
 * em use cases no ciclo 3 (ver `docs/regras-negocio.md`). A FK
 * `RESTRICT` em `projeto_id` está no schema e será respeitada pelo
 * SQLite — esta lane deixa a tradução de `SQLiteConstraintException`
 * em mensagem amigável para a lane de RN.
 */
interface TarefaRepository {

    /** Lista todas as tarefas ativas, reativa (emite a cada insert/update/softDelete). */
    fun getAll(): Flow<List<Tarefa>>

    /** Lista as tarefas de um projeto específico (ativas), reativa. */
    fun getByProjeto(projetoId: String): Flow<List<Tarefa>>

    /** Detalhe de uma tarefa pelo id, reativo. `null` se não existir / soft-deleted. */
    fun getById(id: String): Flow<Tarefa?>

    /** Persiste uma tarefa nova. `id`/`createdAt`/`updatedAt` são de responsabilidade do caller. */
    suspend fun insert(tarefa: Tarefa)

    /** Atualiza uma tarefa existente (mesmo `id`). */
    suspend fun update(tarefa: Tarefa)

    /** Soft delete: marca `deleted_at` sem remover a linha (preserva auditoria local). */
    suspend fun delete(id: String)
}
