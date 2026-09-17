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
 *
 * Convenção desta lane (issue #12 — filtro/busca/ordenação):
 *  - `buscar(...)` é a fonte de dados da lista com busca textual + filtro de
 *    status + ordenação. Recebe os parâmetros já normalizados pela camada
 *    de domínio/ViewModel (termo com `%`, `status` como `name` do enum ou
 *    `null`, `sortBy` como string canônica).
 *  - As queries são estáticas e parametrizadas (sem `LIKE` dinâmico nem
 *    `@RawQuery`) — Room pode validar sintaxe em build time.
 *  - Mantemos `getAll()` original intacto: serve como fallback e é usado por
 *    outros pontos da app (detalhe do projeto, etc.).
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

    // =================================================================================
    // Issue #11 (RN01-RN03) — snapshot por ids para resolver dependencias
    // =================================================================================
    /**
     * Snapshot único de vários projetos pelos ids (ativos). Usado pelo
     * [com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository]
     * (lane RN01-RN03). Ids inexistentes são ignorados. Lista vazia
     * retorna lista vazia sem bater no banco.
     */
    @Query("SELECT * FROM projetos WHERE id IN (:ids) AND deleted_at IS NULL")
    suspend fun getByIdsOnce(ids: List<String>): List<ProjetoEntity>

    // =================================================================================
    // Issue #12 — busca + filtro + ordenação
    // =================================================================================

    /**
     * Lista projetos aplicando busca textual por `nome` (LIKE `%query%`,
     * case-insensitive nativo do SQLite com `LOWER(...)`), filtro opcional
     * de status (passar `null` para listar todos) e ordenação configurável.
     *
     * @param query       termo já com `%` nas pontas (ex.: `%brain%`). Use
     *                    `"%%"` para listar tudo.
     * @param statusFiltro nome do enum `StatusProjeto` (`ABERTO`, `CONCLUIDO`,
     *                    `CANCELADO`) ou `null` para ignorar o filtro.
     * @param sortBy      chave de ordenação canônica:
     *                    - `"nome"`    → `ORDER BY nome COLLATE NOCASE ASC`
     *                    - `"prazo"`   → `ORDER BY prazo_millis ASC` (nulos por último)
     *                    - `"criacao"` → `ORDER BY created_at DESC` (padrão)
     *
     > Implementação: 3 queries estáticas são mais legíveis e seguras que
     > uma única com `CASE WHEN`/SQL dinâmico. Room valida cada uma em
     > build time.
     */
    @Query(
        """
        SELECT * FROM projetos
        WHERE deleted_at IS NULL
          AND (:statusFiltro IS NULL OR status = :statusFiltro)
          AND LOWER(nome) LIKE LOWER(:query)
        ORDER BY nome COLLATE NOCASE ASC
        """
    )
    fun buscarPorNome(
        query: String,
        statusFiltro: String?,
    ): Flow<List<ProjetoEntity>>

    /**
     * Variante de ordenação por prazo. `prazo_millis = NULL` (projetos sem
     * prazo) vão para o final — alinhado com a UX "mostra o que tem
     * urgência primeiro".
     */
    @Query(
        """
        SELECT * FROM projetos
        WHERE deleted_at IS NULL
          AND (:statusFiltro IS NULL OR status = :statusFiltro)
          AND LOWER(nome) LIKE LOWER(:query)
        ORDER BY prazo_millis IS NULL, prazo_millis ASC
        """
    )
    fun buscarPorPrazo(
        query: String,
        statusFiltro: String?,
    ): Flow<List<ProjetoEntity>>

    /**
     * Variante de ordenação por data de criação (mais recentes primeiro).
     * É o fallback usado pela UI quando `sortBy` vier com valor
     * desconhecido / vazio.
     */
    @Query(
        """
        SELECT * FROM projetos
        WHERE deleted_at IS NULL
          AND (:statusFiltro IS NULL OR status = :statusFiltro)
          AND LOWER(nome) LIKE LOWER(:query)
        ORDER BY created_at DESC
        """
    )
    fun buscarPorCriacao(
        query: String,
        statusFiltro: String?,
    ): Flow<List<ProjetoEntity>>
}
