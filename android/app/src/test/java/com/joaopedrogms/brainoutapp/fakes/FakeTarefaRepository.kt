package com.joaopedrogms.brainoutapp.fakes

import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

/**
 * Fake in-memory do [TarefaRepository] para testes JUnit.
 *
 * Ver [FakeProjetoRepository] — mesmo padrão: mantém tarefas num
 * `MutableStateFlow`, sem `android.util.Log`, sem Room. Cobre o
 * contrato todo (Flow + snapshots) para os use cases de regras.
 *
 * **Soft delete:** o fake implementa `delete` como remoção direta
 * (sem soft delete). É o bastante para os testes das lanes RN01-RN03
 * porque nenhum teste cobre o ciclo de vida do soft delete — e se
 * cobrir no futuro, basta substituir a remoção por `state.update { it
 * - id }` (igual a hoje).
 */
class FakeTarefaRepository : TarefaRepository {

    private val state = MutableStateFlow<Map<String, Tarefa>>(emptyMap())

    // ---------- helpers internos ----------

    /** Persiste direto no fake, atualizando `updatedAt`. */
    fun salvar(tarefa: Tarefa): Tarefa {
        val comTimestamp = tarefa.copy(updatedAt = Instant.now())
        state.update { it + (comTimestamp.id to comTimestamp) }
        return comTimestamp
    }

    fun softDelete(id: String) {
        state.update { it - id }
    }

    // ---------- contrato ----------

    override fun getAll(): Flow<List<Tarefa>> =
        state.map { it.values.sortedByDescending { t -> t.updatedAt } }

    override fun getByProjeto(projetoId: String): Flow<List<Tarefa>> =
        state.map { snap ->
            snap.values
                .filter { it.projetoId == projetoId }
                .sortedByDescending { it.updatedAt }
        }

    override fun getById(id: String): Flow<Tarefa?> =
        state.map { it[id] }

    override suspend fun getByIdOnce(id: String): Tarefa? =
        state.value[id]

    /** Helper de teste: snapshot único de **todas** as tarefas (ativas). */
    fun getAllOnce(): List<Tarefa> = state.value.values.toList()

    /** Helper de teste: snapshot único das tarefas de um projeto. */
    fun snapshotByProjeto(projetoId: String): List<Tarefa> =
        state.value.values.filter { it.projetoId == projetoId }

    override suspend fun getByProjetoOnce(projetoId: String): List<Tarefa> =
        state.value.values.filter { it.projetoId == projetoId }

    override suspend fun getByIdsOnce(ids: List<String>): List<Tarefa> {
        if (ids.isEmpty()) return emptyList()
        val snapshot = state.value
        return ids.mapNotNull { snapshot[it] }
    }

    override suspend fun insert(tarefa: Tarefa) {
        salvar(tarefa)
    }

    override suspend fun update(tarefa: Tarefa) {
        salvar(tarefa)
    }

    override suspend fun delete(id: String) {
        softDelete(id)
    }
}
