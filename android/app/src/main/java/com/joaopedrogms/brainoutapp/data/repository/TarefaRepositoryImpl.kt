package com.joaopedrogms.brainoutapp.data.repository

import android.util.Log
import com.joaopedrogms.brainoutapp.data.local.dao.TarefaDao
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.model.toDomain
import com.joaopedrogms.brainoutapp.domain.model.toEntity
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação local (Room) do [TarefaRepository].
 *
 * Mesmo padrão de [ProjetoRepositoryImpl]:
 *  - DAO já retorna `Flow<TarefaEntity>` (reatividade nativa do Room);
 *  - este impl só precisa fazer `.map { it.toDomain() }` — sem I/O extra.
 *  - o cálculo de `deletedAt` fica centralizado aqui.
 *
 * **RN02 (não aplicada nesta lane):** se o caller tentar inserir uma
 * tarefa com `projeto_id` inexistente (FK), o SQLite joga
 * `SQLiteConstraintException`. O repository **propaga** a exceção; a
 * tradução para mensagem amigável fica em outra camada. Mesmo padrão
 * serve para `delete(projeto)` com tarefas em aberto — fora de escopo.
 */
@Singleton
class TarefaRepositoryImpl @Inject constructor(
    private val dao: TarefaDao,
) : TarefaRepository {

    override fun getAll(): Flow<List<Tarefa>> =
        dao.getAll().map { lista -> lista.map { it.toDomain() } }

    override fun getByProjeto(projetoId: String): Flow<List<Tarefa>> =
        dao.getByProjeto(projetoId).map { lista -> lista.map { it.toDomain() } }

    override fun getById(id: String): Flow<Tarefa?> =
        dao.getById(id).map { it?.toDomain() }

    override suspend fun insert(tarefa: Tarefa) {
        dao.insert(tarefa.toEntity())
        Log.d(TAG, "insert tarefa id=${tarefa.id} projeto=${tarefa.projetoId} titulo='${tarefa.titulo}'")
    }

    override suspend fun update(tarefa: Tarefa) {
        dao.update(tarefa.toEntity())
        Log.d(TAG, "update tarefa id=${tarefa.id}")
    }

    override suspend fun delete(id: String) {
        val agora = Instant.now().toEpochMilli()
        dao.softDelete(id, agora)
        Log.d(TAG, "softDelete tarefa id=$id")
    }

    private companion object {
        const val TAG = "TarefaRepository"
    }
}
