package com.joaopedrogms.brainoutapp.ui.screens.applock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.security.SecurityPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela de bloqueio (`AppLockScreen`).
 *
 * Responsabilidades:
 *  - validar a senha digitada via [SecurityPreferencesRepository.verifySenha];
 *  - contar tentativas erradas e impor cooldown (defesa contra força-bruta);
 *  - expor o estado para a UI (erro / cooldown / sucesso).
 *
 * Política de tentativas (ADR-0006): **3** falhas → cooldown de **30s**.
 * (O ADR fala em 5; a lane adotou 3 conforme requisito do orquestrador —
 *  fica como o valor prático aqui. O número é parametrizado abaixo para
 *  facilitar ajuste futuro.)
 *
 * Após [unlock] bem-sucedido, o NavHost troca para a próxima tela. O
 * ViewModel **não** persiste esse "desbloqueio" — é só estado em memória
 * (perde na rotação, mas o NavHost já passou). Cold start sempre
 * pede de novo.
 */
@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val securityRepo: SecurityPreferencesRepository,
) : ViewModel() {

    /**
     * Estado visível para a UI.
     *
     *  - [AppLockUiState.Loading]: validando uma tentativa em curso.
     *  - [AppLockUiState.Idle]: nenhuma tentativa pendente.
     *  - [AppLockUiState.Cooldown]: bloqueado até [remainingSeconds].
     *  - [AppLockUiState.Error]: última tentativa falhou; UI exibe
     *    mensagem e mantém contador.
     */
    sealed interface AppLockUiState {
        data object Idle : AppLockUiState
        data object Loading : AppLockUiState
        data class Cooldown(val remainingSeconds: Int) : AppLockUiState
        data class Error(val message: String, val attemptsLeft: Int) : AppLockUiState
    }

    private val _uiState = MutableStateFlow<AppLockUiState>(AppLockUiState.Idle)
    val uiState: StateFlow<AppLockUiState> = _uiState.asStateFlow()

    /**
     * Tentativas erradas acumuladas. Resetadas em [unlock] bem-sucedido.
     * Volátil: ao matar o processo a contagem zera (decisão consciente —
     *  força-bruta requer processo vivo; o atacante que matar o app
     *  reinicia o cooldown também).
     */
    private var failedAttempts: Int = 0

    /** Evento one-shot: `true` quando a senha foi validada. */
    private val _unlocked = MutableStateFlow(false)
    val unlocked: StateFlow<Boolean> = _unlocked.asStateFlow()

    /**
     * Tenta validar [senha]. Em caso de sucesso emite `_unlocked = true`
     * e a UI chama `rootViewModel.refresh()` para avançar para a home.
     *
     * @return `true` se a senha bate; `false` caso contrário (ou se o
     *         usuário está em cooldown e tentou de novo).
     */
    fun unlock(senha: String): Boolean {
        val state = _uiState.value
        if (state is AppLockUiState.Loading || state is AppLockUiState.Cooldown) {
            return false
        }
        if (senha.length < MIN_SENHA_LEN) {
            _uiState.value = AppLockUiState.Error(
                message = "Senha deve ter pelo menos $MIN_SENHA_LEN caracteres",
                attemptsLeft = (MAX_FAILED_ATTEMPTS - failedAttempts).coerceAtLeast(0),
            )
            return false
        }
        _uiState.value = AppLockUiState.Loading
        viewModelScope.launch {
            val ok = try {
                securityRepo.verifySenha(senha)
            } catch (t: Throwable) {
                Log.e(TAG, "Falha ao verificar senha", t)
                _uiState.value = AppLockUiState.Error(
                    message = t.message ?: "Erro ao validar senha",
                    attemptsLeft = (MAX_FAILED_ATTEMPTS - failedAttempts).coerceAtLeast(0),
                )
                return@launch
            }
            if (ok) {
                failedAttempts = 0
                _unlocked.value = true
                _uiState.value = AppLockUiState.Idle
                Log.i(TAG, "AppLock desbloqueado")
            } else {
                failedAttempts += 1
                val remaining = (MAX_FAILED_ATTEMPTS - failedAttempts).coerceAtLeast(0)
                if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                    startCooldown()
                } else {
                    _uiState.value = AppLockUiState.Error(
                        message = "Senha incorreta",
                        attemptsLeft = remaining,
                    )
                }
            }
        }
        return false // sucesso é assíncrono; quem decide navegar observa `unlocked`.
    }

    /**
     * "Esqueci a senha" — apaga o hash. Após isso o NavHost chamará
     * `refresh()` e o usuário cai na home (perfil preservado; só o
     * AppLock some). Conforme ADR-0006, **não há recovery**: a senha
     * não bloqueia o banco, só a UI.
     */
    fun forgotSenha() {
        viewModelScope.launch {
            try {
                securityRepo.clearLock()
                _unlocked.value = true
                _uiState.value = AppLockUiState.Idle
                Log.w(TAG, "AppLock removido via 'Esqueci a senha'")
            } catch (t: Throwable) {
                Log.e(TAG, "Falha ao limpar AppLock", t)
                _uiState.value = AppLockUiState.Error(
                    message = t.message ?: "Erro ao remover bloqueio",
                    attemptsLeft = (MAX_FAILED_ATTEMPTS - failedAttempts).coerceAtLeast(0),
                )
            }
        }
    }

    /**
     * Cooldown: emite um contador regressivo segundo a segundo. Quando
     * chega a 0, volta para [AppLockUiState.Error] (para o usuário tentar
     * de novo, com `attemptsLeft = MAX_FAILED_ATTEMPTS`).
     */
    private fun startCooldown() {
        viewModelScope.launch {
            for (remaining in COOLDOWN_SECONDS downTo 1) {
                _uiState.value = AppLockUiState.Cooldown(remainingSeconds = remaining)
                delay(1_000L)
            }
            // Cooldown terminou — zera contador e libera a próxima tentativa.
            failedAttempts = 0
            _uiState.value = AppLockUiState.Idle
        }
    }

    /** Reseta o estado local. Útil se a UI for descartada. */
    fun clearError() {
        _uiState.update {
            if (it is AppLockUiState.Error) AppLockUiState.Idle else it
        }
    }

    private companion object {
        const val TAG = "AppLockViewModel"
        const val MIN_SENHA_LEN = 4
        const val MAX_FAILED_ATTEMPTS = 3
        const val COOLDOWN_SECONDS = 30
    }
}
