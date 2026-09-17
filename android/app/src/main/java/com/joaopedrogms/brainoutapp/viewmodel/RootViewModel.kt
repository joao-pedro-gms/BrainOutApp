package com.joaopedrogms.brainoutapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.preferences.PerfilPreferencesRepository
import com.joaopedrogms.brainoutapp.data.security.SecurityPreferencesRepository
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
 *  - [NeedsUnlock]    → perfil definido **e** AppLock ativo
 *                        → tela de bloqueio standalone (ver
 *                        `ui/screens/applock/AppLockScreen.kt`).
 *                        Após o unlock, `refresh()` é chamado e o
 *                        estado passa a [Authenticated].
 *  - [Authenticated]  → perfil definido (e, se havia senha, já
 *                        desbloqueado nesta sessão) → start = `projetos`.
 *
 * Esse mapeamento vive aqui (e não no `NavHost` diretamente) porque o
 * NavHost exige uma `String` resolvida antes do primeiro compose — uma
 * `suspend` na decisão do `startDestination` não é possível. O
 * `BrainOutAppNavHost` então aguarda [RootViewModel.state] virar não-
 * Loading antes de montar o `NavHost` (e trata `NeedsUnlock` como uma
 * tela **fora** do NavHost, conforme ADR-0006).
 *
 * Origem: ADR-0006 — fluxo do cold start.
 */
sealed interface RootStartState {
    data object Loading : RootStartState
    data object NeedsOnboarding : RootStartState
    data object NeedsUnlock : RootStartState
    data object Authenticated : RootStartState
}

@HiltViewModel
class RootViewModel @Inject constructor(
    private val perfilRepo: PerfilPreferencesRepository,
    private val securityRepo: SecurityPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RootStartState>(RootStartState.Loading)
    val state: StateFlow<RootStartState> = _state.asStateFlow()

    init {
        refresh()
    }

    /**
     * Relê o perfil do DataStore e o estado de AppLock, e atualiza
     * [state]. Idempotente — chamado no `init` e sempre que algo
     * muda (ex.: Configurações alterar o perfil, ou o AppLock ser
     * desbloqueado/removido).
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
                val lockEnabled = try {
                    securityRepo.isLockEnabled()
                } catch (t: Throwable) {
                    // Falha ao ler prefs cifradas (KeyStore corrompido, etc.)
                    // → não bloqueia o app. Log fica para diagnóstico.
                    android.util.Log.w("RootViewModel", "Falha ao ler AppLock", t)
                    false
                }
                if (lockEnabled) RootStartState.NeedsUnlock else RootStartState.Authenticated
            }
        }
    }
}
