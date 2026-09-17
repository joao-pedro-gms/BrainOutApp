package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
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
 * ViewModel da tela **Projetos (lista)**.
 *
 * Mantém um [StateFlow] de [UiState] com a lista reativa do DAO do Room.
 *  - `Loading` inicial na primeira coleta.
 *  - `Success(List)` com a lista (vazia ou preenchida — empty state fica na UI).
 *  - `Error` se o flow do Room emitir uma exceção (raro, mas defensivo).
 *
 * **Busca + filtro + ordenação (issue #12):**
 *  - `_query: MutableStateFlow<String>` — termo digitado. Aplicamos
 *    [debounce] de 300 ms antes de chegar ao banco (evita inundar o Room a
 *    cada tecla).
 *  - `_statusFiltro: MutableStateFlow<StatusProjeto?>` — `null` = sem
 *    filtro.
 *  - `_sortBy: MutableStateFlow<ProjetoSortBy>` — NOME (default) / PRAZO /
 *    CRIACAO.
 *  - O `combine` + `flatMapLatest` escolhe qual método do DAO chamar
 *    (`buscarPorNome` / `buscarPorPrazo` / `buscarPorCriacao`). Resultado
 *    é mapeado entity→domain.
 *
 * Sem ações de escrita aqui — `Criar`/`Editar`/`Excluir` moram em
 * `ProjetoFormViewModel` e `ProjetoDetailViewModel`.
 *
 * > Esta lane consome o DAO diretamente porque a interface
 * > `ProjetoRepository` (em `domain/repository/`) está fora do escopo da
 * > lane filtro-busca. Quando o contrato for estendido em outra lane,
 * > basta mover `buscar(...)` para o repositório e trocar o
 * > `projetoDao` por um `projetoRepository.buscar(...)` aqui — nenhuma
 * > mudança na UI.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ProjetoListViewModel @Inject constructor(
    private val projetoDao: ProjetoDao,
) : ViewModel() {

    /** Termo digitado pelo usuário (sem debounce). */
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /** Filtro por status. `null` = listar todos. */
    private val _statusFiltro = MutableStateFlow<StatusProjeto?>(null)
    val statusFiltro: StateFlow<StatusProjeto?> = _statusFiltro.asStateFlow()

    /** Ordenação aplicada à lista. */
    private val _sortBy = MutableStateFlow(ProjetoSortBy.NOME)
    val sortBy: StateFlow<ProjetoSortBy> = _sortBy.asStateFlow()

    /**
     * Lista final exibida pela UI.
     *
     * Pipeline:
     *  1. `_query.debounce(300ms)` — só propaga o termo se ele ficou
     *     estável por 300ms.
     *  2. `combine` com `_statusFiltro` e `_sortBy` — quando QUALQUER dos
     *     três muda, refaz a query.
     *  3. `flatMapLatest` — cancela a query anterior quando uma nova
     *     chega (evita race condition).
     *  4. `.map { entityList -> entityList.map { it.toDomain() } }` — para
     *     o domínio.
     *  5. `.catch` — qualquer falha vira `UiState.Error`.
     */
    val state: StateFlow<UiState<List<Projeto>>> = combine(
        _query.debounce(DEBOUNCE_MS),
        _statusFiltro,
        _sortBy,
    ) { q, status, sort ->
        Triple(q, status, sort)
    }
        .flatMapLatest { (q, status, sort) ->
            val likeTerm = "%${q.trim()}%"
            val statusName = status?.name
            when (sort) {
                ProjetoSortBy.NOME -> projetoDao.buscarPorNome(likeTerm, statusName)
                ProjetoSortBy.PRAZO -> projetoDao.buscarPorPrazo(likeTerm, statusName)
                ProjetoSortBy.CRIACAO -> projetoDao.buscarPorCriacao(likeTerm, statusName)
            }
        }
        .map { lista ->
            UiState.Success(lista.map { it.toDomain() }) as UiState<List<Projeto>>
        }
        .catch { ex ->
            emit(UiState.Error(ex.message ?: "Erro ao listar projetos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /** Atualiza o termo de busca (UI). */
    fun atualizarQuery(nova: String) {
        _query.value = nova
    }

    /** Define (ou limpa) o filtro de status. */
    fun atualizarStatusFiltro(status: StatusProjeto?) {
        _statusFiltro.value = status
    }

    /** Troca a ordenação. */
    fun atualizarSortBy(sort: ProjetoSortBy) {
        _sortBy.value = sort
    }

    companion object {
        /** Debounce aplicado à busca textual — 300 ms. */
        const val DEBOUNCE_MS: Long = 300L
    }
}

/** Opções de ordenação da lista de projetos (issue #12). */
enum class ProjetoSortBy {
    NOME,
    PRAZO,
    CRIACAO;
}
