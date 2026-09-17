package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.usecase.ExcluirProjetoUseCase
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
 * ViewModel da tela **Detalhes do projeto** (`detalhes/{projetoId}`).
 *
 * Responsabilidades:
 *  - expor o projeto reativamente (`StateFlow<UiState<Projeto?>>`);
 *  - disparar `excluir()` (soft delete) e emitir evento de navegação
 *    `ProjetoExcluido` via Channel (one-shot, não acumulado em state).
 *
 * > Importante: o `id` vem do `SavedStateHandle` da rota de navegação —
 * > dessa forma a tela não precisa receber o id por parâmetro no construtor
 * > do Composable, e funciona corretamente com `remember`/config changes.
 */
@HiltViewModel
class ProjetoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: ProjetoRepository,
    private val excluirProjeto: ExcluirProjetoUseCase,
) : ViewModel() {

    /** Evento de UI consumido uma única vez (não parte do `state`). */
    sealed interface Event {
        data object ProjetoExcluido : Event
    }

    private val projetoId: String =
        savedStateHandle.get<String>(Destinations.DETALHES_PROJETO_ARG).orEmpty()

    private val _event = Channel<Event>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    val state: StateFlow<UiState<Projeto?>> = repository.getById(projetoId)
        .map<Projeto?, UiState<Projeto?>> { UiState.Success(it) }
        .catch { ex -> emit(UiState.Error(ex.message ?: "Erro ao carregar projeto")) }
        .stateIn(
            scope = viewModelScope,
            // WhileSubscribed(5_000) é o padrão recomendado: mantém o flow ativo
            // 5s após o último consumer sair, para sobreviver a config changes
            // sem desperdiçar recursos quando a tela sai de cena.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /**
     * Soft delete do projeto atual.
     *
     * Após o delete, emite [Event.ProjetoExcluido] para que a tela faça
     * `popBackStack()` de volta à lista.
     */
    fun excluir() {
        viewModelScope.launch {
            runCatching { excluirProjeto(projetoId) }
                .onSuccess { _event.send(Event.ProjetoExcluido) }
                .onFailure {
                    _event.send(Event.ProjetoExcluido)
                    // Estado defensivo: se o delete falhou, ainda assim voltamos
                    // para evitar o usuário preso na tela de detalhe. Log fica
                    // no repository; aqui só navegamos.
                }
        }
    }
}