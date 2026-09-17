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
 * Sem regra de negócio aqui: validações de RN01-RN03 serão injetadas
 * em use cases no ciclo 3 (ver `docs/regras-negocio.md`).
 */
interface ProjetoRepository {

    /** Lista de projetos ativos, reativa (emite a cada insert/update/softDelete). */
    fun getAll(): Flow<List<Projeto>>

    /** Detalhe de um projeto pelo id, reativo. `null` se não existir / soft-deleted. */
    fun getById(id: String): Flow<Projeto?>

    /** Persiste um projeto novo. `id`/`createdAt`/`updatedAt` são de responsabilidade do caller. */
    suspend fun insert(projeto: Projeto)

    /** Atualiza um projeto existente (mesmo `id`). */
    suspend fun update(projeto: Projeto)

    /** Soft delete: marca `deleted_at` sem remover a linha (preserva auditoria local). */
    suspend fun delete(id: String)
}