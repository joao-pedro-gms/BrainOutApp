package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import com.joaopedrogms.brainoutapp.domain.usecase.ExcluirTarefaUseCase
import com.joaopedrogms.brainoutapp.ui.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela **Detalhes da tarefa** (`criacao/tarefa/{tarefaId?}`).
 *
 * Mesmo padrão do [ProjetoDetailViewModel]:
 *  - `id` vem do [SavedStateHandle] da rota;
 *  - expõe o projeto reativamente (`StateFlow<UiState<Tarefa?>>`);
 *  - `excluir()` dispara soft delete + evento one-shot `TarefaExcluida`
 *    via [Channel] para a tela voltar à lista.
 */
@HiltViewModel
class TarefaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: TarefaRepository,
    private val excluirTarefa: ExcluirTarefaUseCase,
) : ViewModel() {

    /** Evento de UI consumido uma única vez (não parte do `state`). */
    sealed interface Event {
        data object TarefaExcluida : Event
    }

    private val tarefaId: String =
        savedStateHandle.get<String>(Destinations.CRIACAO_TAREFA_ARG).orEmpty()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    val state: StateFlow<UiState<Tarefa?>> = repository.getById(tarefaId)
        .map<Tarefa?, UiState<Tarefa?>> { UiState.Success(it) }
        .catch { ex -> emit(UiState.Error(ex.message ?: "Erro ao carregar tarefa")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /**
     * Soft delete da tarefa atual.
     *
     > **Pendência:** esta lane não cria uma rota `detalhes/tarefa/{id}`
     > > dedicada — a tela atual de tarefas mostra lista, e o form é
     > > compartilhado com a rota `criacao/tarefa/{tarefaId?}`. Quando
     > > uma lane posterior criar uma `DetalhesTarefaScreen`, este VM
     > > será plugado nela.
     */
    fun excluir() {
        viewModelScope.launch {
            runCatching { excluirTarefa(tarefaId) }
                .onSuccess { _event.send(Event.TarefaExcluida) }
                .onFailure {
                    _event.send(Event.TarefaExcluida)
                    // Defensivo: se o delete falhou, voltamos mesmo assim
                    // para evitar o usuário preso na tela. O log fica no
                    // repository.
                }
        }
    }
}
