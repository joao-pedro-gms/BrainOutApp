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
 *     recebe atualização automática após cada `insert/update/softDelete`.
 *
 * **Adições da lane RN01-RN03 (issue #11):**
 *  - `getByIdsOnce(ids)`: snapshot síncrono usado por
 *    [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirTarefaUseCase]
 *    para carregar dependências sem ficar preso a um `Flow` único. Filtra
 *    `deleted_at IS NULL` para não contar soft-deleteds como dependência
 *    ativa (decisão: dependência removida = dependência satisfeita).
 *  - `getByProjetoOnce(projetoId)`: snapshot síncrono para
 *    [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirProjetoUseCase]
 *    contar tarefas em aberto (RN02). A versão `Flow` continua existindo
 *    para a UI (reatividade).
 *
 * **RN02 (não implementada nesta lane):** a FK `RESTRICT` em
 * `projeto_id` joga `SQLiteConstraintException` se o caller tentar
 * excluir um projeto com tarefas em aberto. Esta lane **não** converte
 * a exceção — fica para a lane de regras de negócio (RN01-03).
 *
 * Convenção desta lane (issue #12 — filtro/busca/ordenação):
 *  - `buscar(...)` aplica busca textual por `titulo` + ordenação.
 *  - Filtro de status já existe via `TarefaListViewModel.combine` (issue #10);
 *    mantemos aqui só busca + sort para evitar combinatorial explosion de
 *    queries no DAO. Status é aplicado no domínio.
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
     */
    @Query("SELECT * FROM tarefas WHERE deleted_at IS NULL ORDER BY updated_at DESC")
    fun getAll(): Flow<List<TarefaEntity>>

    /**
     * Lista as tarefas de um projeto específico (ativas), ordenada por
     * `updated_at` desc. Usada pelo detalhe do projeto.
     *
     * @param projetoId FK do projeto pai.
     */
    @Query("SELECT * FROM tarefas WHERE projeto_id = :projetoId AND deleted_at IS NULL ORDER BY updated_at DESC")
    fun getByProjeto(projetoId: String): Flow<List<TarefaEntity>>

    // =================================================================================
    // Issue #11 (RN01-RN03) — snapshots para regras de negocio
    // =================================================================================
    /**
     * Snapshot único das tarefas de um projeto — usado por
     * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirProjetoUseCase]
     * (RN02) para contar tarefas em aberto sem precisar de `Flow`.
     *
     * > Retorna **todas** as tarefas ativas do projeto (não só as
     * > abertas). Quem chama decide o predicado de "aberta" — geralmente
     * > `status IN ('ABERTA','EM_ANDAMENTO')`.
     */
    @Query("SELECT * FROM tarefas WHERE projeto_id = :projetoId AND deleted_at IS NULL")
    suspend fun getByProjetoOnce(projetoId: String): List<TarefaEntity>

    /**
     * Snapshot único de várias tarefas por id — usado por
     * [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirTarefaUseCase]
     * (RN01) para inspecionar dependências.
     *
     * - Filtra `deleted_at IS NULL` — dependência soft-deleted é
     *   tratada como "satisfeita" (a tarefa-mãe não bloqueia mais).
     * - Ids inexistentes são simplesmente ignorados (não jogam erro).
     * - `ids` vazio retorna lista vazia sem bater no banco.
     */
    @Query("SELECT * FROM tarefas WHERE id IN (:ids) AND deleted_at IS NULL")
    suspend fun getByIdsOnce(ids: List<String>): List<TarefaEntity>

    // =================================================================================
    // Issue #12 — busca + ordenação
    // =================================================================================

    /**
     * Lista tarefas aplicando busca textual por `titulo` (LIKE `%query%`,
     * case-insensitive). Ordenação por prazo — tarefas sem prazo vão para o
     * final.
     *
     * @param query termo já com `%` nas pontas. Use `"%%"` para listar tudo.
     */
    @Query(
        """
        SELECT * FROM tarefas
        WHERE deleted_at IS NULL
          AND LOWER(titulo) LIKE LOWER(:query)
        ORDER BY prazo_millis IS NULL, prazo_millis ASC
        """
    )
    fun buscarPorPrazo(query: String): Flow<List<TarefaEntity>>

    /**
     * Ordenação por prioridade (urgente → alta → média → baixa).
     *
     * SQLite ordena `TEXT` alfabeticamente, o que daria ALTA/BAIXA/MEDIA/
     * URGENTE — **não** o que queremos. Por isso usamos `CASE WHEN` para
     * rank numérico explícito:
     *
     *  - URGENTE → 1
     *  - ALTA    → 2
     *  - MEDIA   → 3
     *  - BAIXA   → 4
     *
     * `created_at DESC` é tie-breaker para prioridades iguais.
     */
    @Query(
        """
        SELECT * FROM tarefas
        WHERE deleted_at IS NULL
          AND LOWER(titulo) LIKE LOWER(:query)
        ORDER BY
            CASE prioridade
                WHEN 'URGENTE' THEN 1
                WHEN 'ALTA'    THEN 2
                WHEN 'MEDIA'   THEN 3
                WHEN 'BAIXA'   THEN 4
                ELSE 5
            END ASC,
            created_at DESC
        """
    )
    fun buscarPorPrioridade(query: String): Flow<List<TarefaEntity>>

    /**
     * Ordenação alfabética por título (case-insensitive). É o fallback
     * usado pela UI quando `sortBy` vier com valor desconhecido.
     */
    @Query(
        """
        SELECT * FROM tarefas
        WHERE deleted_at IS NULL
          AND LOWER(titulo) LIKE LOWER(:query)
        ORDER BY titulo COLLATE NOCASE ASC
        """
    )
    fun buscarPorTitulo(query: String): Flow<List<TarefaEntity>>
}
