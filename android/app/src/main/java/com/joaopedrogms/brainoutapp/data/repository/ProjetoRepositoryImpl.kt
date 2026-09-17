package com.joaopedrogms.brainoutapp.data.repository

import android.util.Log
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.toDomain
import com.joaopedrogms.brainoutapp.domain.model.toEntity
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação local (Room) do [ProjetoRepository].
 *
 * Por que **não** um `suspend fun Flow`?
 *  - O DAO já retorna `Flow<ProjetoEntity>` (reatividade nativa do Room).
 *  - Este impl só precisa fazer `.map { it.toDomain() }` — sem I/O extra.
 *
 * Centralizamos o cálculo de `deletedAt` aqui: o repository garante que
 * o timestamp usado é coerente (`Instant.now()` no momento do delete),
 * e não uma mistura de `System.currentTimeMillis()` espalhada.
 */
@Singleton
class ProjetoRepositoryImpl @Inject constructor(
    private val dao: ProjetoDao,
) : ProjetoRepository {

    override fun getAll(): Flow<List<Projeto>> =
        dao.getAll().map { lista -> lista.map { it.toDomain() } }

    override fun getById(id: String): Flow<Projeto?> =
        dao.getById(id).map { it?.toDomain() }

    override suspend fun insert(projeto: Projeto) {
        dao.insert(projeto.toEntity())
        Log.d(TAG, "insert projeto id=${projeto.id} nome='${projeto.nome}'")
    }

    override suspend fun update(projeto: Projeto) {
        dao.update(projeto.toEntity())
        Log.d(TAG, "update projeto id=${projeto.id}")
    }

    override suspend fun delete(id: String) {
        val agora = Instant.now().toEpochMilli()
        dao.softDelete(id, agora)
        Log.d(TAG, "softDelete projeto id=$id")
    }

    private companion object {
        const val TAG = "ProjetoRepository"
    }
}