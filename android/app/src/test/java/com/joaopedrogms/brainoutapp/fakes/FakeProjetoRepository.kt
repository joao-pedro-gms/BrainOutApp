package com.joaopedrogms.brainoutapp.fakes

import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

/**
 * Fake in-memory do [ProjetoRepository] para testes JUnit.
 *
 * **Por que existe:** as lanes de regras de negócio (RN01-RN03) testam
 * use cases sem precisar de Room. O fake mantém projetos num
 * `MutableStateFlow` (reatividade preservada) e expõe snapshots
 * síncronos para os métodos `*Once`.
 *
 * **Não é Android-aware:** deliberadamente evita `android.util.Log`
 * (que exige stub do Robolectric em testes unitários JVM). O preço é
 * não ter logs; o ganho é rodar com `./gradlew testDebugUnitTest` puro.
 *
 * **Uso típico:**
 * ```
 * val repo = FakeProjetoRepository()
 * repo.salvar(Projeto(nome = "A", ...))
 * val useCase = ConcluirProjetoUseCase(repo, tarefaRepoFake)
 * val result = useCase(projetoId)
 * ```
 */
class FakeProjetoRepository : ProjetoRepository {

    private val state = MutableStateFlow<Map<String, Projeto>>(emptyMap())

    // ---------- helpers internos (não fazem parte do contrato) ----------

    /** Persiste direto no fake, atualizando `updatedAt`. */
    fun salvar(projeto: Projeto): Projeto {
        val comTimestamp = projeto.copy(updatedAt = Instant.now())
        state.update { it + (comTimestamp.id to comTimestamp) }
        return comTimestamp
    }

    /** Remove (simula soft delete do impl real). */
    fun softDelete(id: String) {
        state.update { it - id }
    }

    // ---------- contrato ----------

    override fun getAll(): Flow<List<Projeto>> =
        state.map { it.values.sortedByDescending { p -> p.updatedAt } }

    override fun getById(id: String): Flow<Projeto?> =
        state.map { it[id] }

    override suspend fun getByIdOnce(id: String): Projeto? =
        state.value[id]

    override suspend fun getByIdsOnce(ids: List<String>): List<Projeto> {
        if (ids.isEmpty()) return emptyList()
        val snapshot = state.value
        return ids.mapNotNull { snapshot[it] }
    }

    override suspend fun insert(projeto: Projeto) {
        salvar(projeto)
    }

    override suspend fun update(projeto: Projeto) {
        salvar(projeto)
    }

    override suspend fun delete(id: String) {
        softDelete(id)
    }
}
