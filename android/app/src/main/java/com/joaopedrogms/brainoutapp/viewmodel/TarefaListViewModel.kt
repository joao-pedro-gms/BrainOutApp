package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.usecase.ListarTarefasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel da tela **Tarefas (lista)** (`tarefas`).
 *
 * Mantém um [StateFlow] de [UiState] com a lista reativa do repository.
 *  - `Loading` inicial na primeira coleta.
 *  - `Success(List)` com a lista (vazia ou preenchida — empty state fica na UI).
 *  - `Error` se o flow do Room emitir uma exceção (raro, mas defensivo).
 *
 * **Filtro de status** (issue #10): o usuário pode filtrar por status via
 * [atualizarFiltroStatus]. Quando o filtro é `null`, lista tudo; quando
 * definido, lista só as tarefas naquele estado. O filtro é local à UI
 * (não persiste em DataStore) e é aplicado no `combine` com a lista
 * vinda do repositório.
 *
 * Sem ações de escrita aqui — `Criar` / `Editar` / `Excluir` moram em
 * `TarefaFormViewModel` e `TarefaDetailViewModel`.
 */
@HiltViewModel
class TarefaListViewModel @Inject constructor(
    private val listarTarefas: ListarTarefasUseCase,
) : ViewModel() {

    /** Filtro opcional por status. `null` = sem filtro. */
    private val _filtroStatus = MutableStateFlow<StatusTarefa?>(null)
    val filtroStatus: StateFlow<StatusTarefa?> = _filtroStatus.asStateFlow()

    /**
     * Lista final aplicada: tarefas ativas do repositório + filtro de status.
     * Erros do `Flow` do Room são convertidos em `UiState.Error`.
     */
    val state: StateFlow<UiState<List<Tarefa>>> = combine(
        listarTarefas(),
        _filtroStatus,
    ) { todas, filtro ->
        val filtradas = if (filtro == null) todas else todas.filter { it.status == filtro }
        UiState.Success(filtradas) as UiState<List<Tarefa>>
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    /** Define (ou limpa) o filtro de status aplicado à lista. */
    fun atualizarFiltroStatus(status: StatusTarefa?) {
        _filtroStatus.value = status
    }
}
