package com.joaopedrogms.brainoutapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para a tabela `projetos`.
 *
 * Convenção desta lane (issue #8):
 *  - Toda leitura filtra `deleted_at IS NULL` — soft delete nunca é exposto à UI.
 *  - `insert` / `update` são chamados pela camada de repository, que centraliza
 *    o cálculo de `updated_at` (evita drift entre callers).
 *  - `softDelete` grava `deleted_at` em vez de remover a linha, preservando
 *    histórico para auditoria local (R5 — offline + persistência local).
 *  - `Flow` nas listas para reatividade Compose: o `collectAsState` da UI
 *     recebe atualização automática após cada `insert/update/softDelete`.
 */
@Dao
interface ProjetoDao {

    /** Insere um projeto novo. Em conflito de PK, substitui. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(projeto: ProjetoEntity)

    /** Atualiza um projeto existente (mesmo `id`). */
    @Update
    suspend fun update(projeto: ProjetoEntity)

    /**
     * Soft delete: marca a linha como removida sem apagá-la.
     * @param id      identificador do projeto
     * @param deletedAt epoch millis UTC do momento da exclusão
     */
    @Query("UPDATE projetos SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :id AND deleted_at IS NULL")
    suspend fun softDelete(id: String, deletedAt: Long)

    /**
     * Carrega um projeto pelo `id`, **excluindo** soft-deleted.
     * Retorna `null` se não existir ou se estiver removido.
     */
    @Query("SELECT * FROM projetos WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    fun getById(id: String): Flow<ProjetoEntity?>

    /** Versão `suspend` (snapshot único) — usada pelo form no carregamento inicial. */
    @Query("SELECT * FROM projetos WHERE id = :id AND deleted_at IS NULL LIMIT 1")
    suspend fun getByIdOnce(id: String): ProjetoEntity?

    /**
     * Lista todos os projetos ativos, ordenada por `updated_at` desc
     * (mais recentemente alterado aparece primeiro — UX de inbox).
     */
    @Query("SELECT * FROM projetos WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun getAll(): Flow<List<ProjetoEntity>>

    /**
     * Snapshot único (não-Flow) — útil para testes ou exportação futura.
     */
    @Query("SELECT * FROM projetos WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    suspend fun getAllOnce(): List<ProjetoEntity>
}