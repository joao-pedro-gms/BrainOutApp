package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.local.dao.TarefaDao
import com.joaopedrogms.brainoutapp.data.local.entity.TarefaEntity
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel da tela **Tarefas (lista)** (`tarefas`).
 *
 * Mantém um [StateFlow] de [UiState] com a lista reativa do DAO do Room.
 *  - `Loading` inicial na primeira coleta.
 *  - `Success(List)` com a lista (vazia ou preenchida — empty state fica na UI).
 *  - `Error` se o flow do Room emitir uma exceção (raro, mas defensivo).
 *
 * **Busca + filtro de status + ordenação (issue #12 — extensão da issue #10):**
 *  - `_query: MutableStateFlow<String>` — termo digitado pelo usuário.
 *    Aplicamos [debounce] de 300 ms antes de chegar ao banco.
 *  - `_filtroStatus: MutableStateFlow<StatusTarefa?>` — herdado da issue #10.
 *    Filtro é aplicado **no domínio** (em memória) após o `combine`, porque
 *    o DAO tem 1 query de busca e 3 ordenações; combinar status × sort
 *    daria 6 variantes estáticas. Manter status no `combine` do domínio
 *    fica mais legível e barato (lista de tarefas é menor que a de
 *    projetos).
 *  - `_sortBy: MutableStateFlow<TarefaSortBy>` — PRAZO (default) /
 *    PRIORIDADE / TITULO.
 *
 * Sem ações de escrita aqui — `Criar` / `Editar` / `Excluir` moram em
 * `TarefaFormViewModel` e `TarefaDetailViewModel`.
 *
 * > Esta lane consome o DAO diretamente porque a interface
 * > `TarefaRepository` (em `domain/repository/`) está fora do escopo da
 * > lane filtro-busca. Quando o contrato for estendido em outra lane,
 * > basta mover `buscar(...)` para o repositório e trocar o `tarefaDao`
 * > por `tarefaRepository.buscar(...)` aqui — nenhuma mudança na UI.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TarefaListViewModel @Inject constructor(
    private val tarefaDao: TarefaDao,
) : ViewModel() {

    /** Filtro opcional por status. `null` = sem filtro (herdado da issue #10). */
    private val _filtroStatus = MutableStateFlow<StatusTarefa?>(null)
    val filtroStatus: StateFlow<StatusTarefa?> = _filtroStatus.asStateFlow()

    /** Termo de busca (sem debounce). Aplicado no DAO via `LIKE`. */
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /** Ordenação aplicada à lista. */
    private val _sortBy = MutableStateFlow(TarefaSortBy.PRAZO)
    val sortBy: StateFlow<TarefaSortBy> = _sortBy.asStateFlow()

    /**
     * Lista filtrada por status (após busca + sort já aplicados pelo DAO).
     * Pipeline:
     *  1. `combine(query.debounce, sortBy)` → escolhe qual método do DAO
     *     chamar.
     *  2. `flatMapLatest` cancela a query anterior quando uma nova chega.
     *  3. `combine(...com filtroStatus)` aplica o filtro de status em
     *     memória (já carregada).
     *  4. `.map` para o domínio.
     *  5. `.catch` para erros.
     *
     * É o `state` consumido pela UI.
     */
    val state: StateFlow<UiState<List<Tarefa>>> = combine(
        _query.debounce(DEBOUNCE_MS),
        _sortBy,
    ) { q, sort -> q to sort }
        .flatMapLatest { (q, sort) ->
            val likeTerm = "%${q.trim()}%"
            when (sort) {
                TarefaSortBy.PRAZO -> tarefaDao.buscarPorPrazo(likeTerm)
                TarefaSortBy.PRIORIDADE -> tarefaDao.buscarPorPrioridade(likeTerm)
                TarefaSortBy.TITULO -> tarefaDao.buscarPorTitulo(likeTerm)
            }
        }
        .combine(_filtroStatus) { lista, filtro ->
            if (filtro == null) lista else lista.filter { it.status == filtro.name }
        }
        .map { lista ->
            UiState.Success(lista.map { it.toDomain() }) as UiState<List<Tarefa>>
        }
        .catch { ex ->
            emit(UiState.Error(ex.message ?: "Erro ao listar tarefas"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /** Define (ou limpa) o filtro de status aplicado à lista. */
    fun atualizarFiltroStatus(status: StatusTarefa?) {
        _filtroStatus.value = status
    }

    /** Atualiza o termo de busca. */
    fun atualizarQuery(nova: String) {
        _query.value = nova
    }

    /** Troca a ordenação. */
    fun atualizarSortBy(sort: TarefaSortBy) {
        _sortBy.value = sort
    }

    companion object {
        /** Debounce aplicado à busca textual — 300 ms. */
        const val DEBOUNCE_MS: Long = 300L
    }
}

/** Opções de ordenação da lista de tarefas (issue #12). */
enum class TarefaSortBy {
    PRAZO,
    PRIORIDADE,
    TITULO;
}
