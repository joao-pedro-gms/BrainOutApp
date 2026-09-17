package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.usecase.ListarProjetosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela **Projetos (lista)**.
 *
 * Mantém um [StateFlow] de [UiState] com a lista reativa do repository.
 *  - `Loading` inicial na primeira coleta.
 *  - `Success(List)` com a lista (vazia ou preenchida — empty state fica na UI).
 *  - `Error` se o flow do Room emitir uma exceção (raro, mas defensivo).
 *
 * Sem ações de escrita aqui — `Criar`/`Editar`/`Excluir` moram em
 * `ProjetoFormViewModel` e `ProjetoDetailViewModel` para manter esta tela
 * fina.
 */
@HiltViewModel
class ProjetoListViewModel @Inject constructor(
    private val listarProjetos: ListarProjetosUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Projeto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Projeto>>> = _state.asStateFlow()

    init {
        observar()
    }

    private fun observar() {
        viewModelScope.launch {
            listarProjetos()
                .onEach { lista ->
                    _state.value = UiState.Success(lista)
                }
                .catch { ex ->
                    _state.value = UiState.Error(ex.message ?: "Erro ao listar projetos")
                }
                .collect { /* terminal do flow; .onEach acima já escreveu o state */ }
        }
    }
}