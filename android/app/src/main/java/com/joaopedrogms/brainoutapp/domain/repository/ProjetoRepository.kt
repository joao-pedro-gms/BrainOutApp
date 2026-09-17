package com.joaopedrogms.brainoutapp.domain.repository

import com.joaopedrogms.brainoutapp.domain.model.Projeto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de domínio para persistência de projetos.
 *
 * Implementação fica em `data/repository/ProjetoRepositoryImpl.kt`
 * (ligada via Hilt). Use cases e ViewModels consomem **apenas** esta
 * interface — nada de Room ou detalhes de storage vaza para `domain/`.
 *
 * Adições da lane RN01-RN03 (issue #11):
 *  - [getByIdOnce] — snapshot síncrono usado pelos use cases que
 *    precisam do projeto em uma única leitura (não dá pra usar `Flow`
 *    dentro de um `suspend` puro: pegar o `first()` é caro e instável
 *    entre estados).
 *  - [getByIdsOnce] — usado pelo
 *    [com.joaopedrogms.brainoutapp.domain.usecase.ConcluirTarefaUseCase]
 *    se/quando precisarmos checar tarefas contra múltiplos projetos.
 */
interface ProjetoRepository {

    /** Lista de projetos ativos, reativa (emite a cada insert/update/softDelete). */
    fun getAll(): Flow<List<Projeto>>

    /** Detalhe de um projeto pelo id, reativo. `null` se não existir / soft-deleted. */
    fun getById(id: String): Flow<Projeto?>

    /** Snapshot síncrono de um projeto pelo id. `null` se não existir / soft-deleted. */
    suspend fun getByIdOnce(id: String): Projeto?

    /** Snapshot síncrono de vários projetos pelos ids. Ids inexistentes são ignorados. */
    suspend fun getByIdsOnce(ids: List<String>): List<Projeto>

    /** Persiste um projeto novo. `id`/`createdAt`/`updatedAt` são de responsabilidade do caller. */
    suspend fun insert(projeto: Projeto)

    /** Atualiza um projeto existente (mesmo `id`). */
    suspend fun update(projeto: Projeto)

    /** Soft delete: marca `deleted_at` sem remover a linha (preserva auditoria local). */
    suspend fun delete(id: String)
}
