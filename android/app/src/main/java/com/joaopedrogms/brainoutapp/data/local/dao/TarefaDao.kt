package com.joaopedrogms.brainoutapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joaopedrogms.brainoutapp.data.local.entity.TarefaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para a tabela `tarefas`.
 *
 * Convenção desta lane (issue #10) — espelha [ProjetoDao]:
 *  - Toda leitura filtra `deleted_at IS NULL` — soft delete nunca é exposto à UI.
 *  - `insert` / `update` são chamados pela camada de repository, que centraliza
 *    o cálculo de `updated_at`.
 *  - `softDelete` grava `deleted_at` em vez de remover a linha, preservando
 *    histórico para auditoria local.
 *  - `Flow` nas listas para reatividade Compose: o `collectAsState` da UI
 *    recebe atualização automática após cada `insert/update/softDelete`.
 *
 * **RN02 (não implementada nesta lane):** a FK `RESTRICT` em
 * `projeto_id` joga `SQLiteConstraintException` se o caller tentar
 * excluir um projeto com tarefas em aberto. Esta lane **não** converte
 * a exceção — fica para a lane de regras de negócio (RN01-03).
 */
@Dao
interface TarefaDao {

    /** Insere uma tarefa nova. Em conflito de PK, substitui. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarefa: TarefaEntity)

    /** Atualiza uma tarefa existente (mesmo `id`). */
    @Update
    suspend fun update(tarefa: TarefaEntity)

    /**
     * Soft delete: marca a linha como removida sem apagá-la.
     * @param id        identificador da tarefa.
     * @param deletedAt epoch millis UTC do momento da exclusão.
     */
    @Query("UPDATE tarefas SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :id AND deleted_at IS NULL")
    suspend fun softDelete(id: String, deletedAt: Long)

    /**
     * Carrega uma tarefa pelo `id`, **excluindo** soft-deleted.
     * Retorna `null` se não existir ou se estiver removida.
     */
    @Query("SELECT * FROM tarefas WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    fun getById(id: String): Flow<TarefaEntity?>

    /** Versão `suspend` (snapshot único) — usada pelo form no carregamento inicial. */
    @Query("SELECT * FROM tarefas WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    suspend fun getByIdOnce(id: String): TarefaEntity?

    /**
     * Lista todas as tarefas ativas, ordenada por `updated_at` desc
     * (mais recentemente alterada primeiro — UX de inbox).
     *
     > **Pendência:** filtro por projeto + status moram em métodos
     > específicos (ver [getByProjeto] e — futuro — `getByStatus`).
     */
    @Query("SELECT * FROM tarefas WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun getAll(): Flow<List<TarefaEntity>>

    /**
     * Lista as tarefas de um projeto específico (ativas), ordenada por
     * `updated_at` desc. Usada pelo detalhe do projeto (issue posterior).
     *
     * @param projetoId FK do projeto pai.
     */
    @Query("SELECT * FROM tarefas WHERE projeto_id = :projetoId AND deleted_at IS NULL ORDER BY updated_at DESC")
    fun getByProjeto(projetoId: String): Flow<List<TarefaEntity>>
}
