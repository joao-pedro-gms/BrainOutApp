package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.preferences.PerfilPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado raiz da decisão de start destination.
 *
 *  - [Loading]        → ainda lendo DataStore (cold start).
 *  - [NeedsOnboarding] → perfil indefinido → start = `onboarding`.
 *  - [Authenticated]  → perfil definido → start = `projetos` (home).
 *
 * Esse mapeamento vive aqui (e não no `NavHost` diretamente) porque o
 * NavHost exige uma `String` resolvida antes do primeiro compose — uma
 * `suspend` na decisão do `startDestination` não é possível. O
 * `BrainOutAppNavHost` então aguarda [RootViewModel.state] virar não-
 * Loading antes de montar o `NavHost`.
 *
 * Origem: ADR-0006 — fluxo do cold start.
 */
sealed interface RootStartState {
    data object Loading : RootStartState
    data object NeedsOnboarding : RootStartState
    data object Authenticated : RootStartState
}

@HiltViewModel
class RootViewModel @Inject constructor(
    private val perfilRepo: PerfilPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RootStartState>(RootStartState.Loading)
    val state: StateFlow<RootStartState> = _state.asStateFlow()

    init {
        refresh()
    }

    /**
     * Relê o perfil do DataStore e atualiza [state]. Idempotente —
     * chamado no `init` e pode ser invocado novamente após mudanças
     * (ex.: quando a tela de Configurações alterar o perfil).
     */
    fun refresh() {
        viewModelScope.launch {
            _state.value = RootStartState.Loading
            val perfil = try {
                perfilRepo.getPerfil()
            } catch (t: Throwable) {
                // Falha de leitura (storage cheio, permissão negada, etc.) →
                // trata como "ainda não escolheu" para não bloquear o app.
                null
            }
            _state.value = if (perfil == null) {
                RootStartState.NeedsOnboarding
            } else {
                RootStartState.Authenticated
            }
        }
    }
}
