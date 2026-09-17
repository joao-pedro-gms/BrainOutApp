package com.joaopedrogms.brainoutapp.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.data.preferences.Perfil
import com.joaopedrogms.brainoutapp.data.preferences.PerfilPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela de onboarding (escolha de perfil local).
 *
 * Origem: ADR-0006 — perfil local opcional, definido uma única vez na
 * primeira execução do app (pode ser alterado depois em Configurações).
 *
 * R12: a UI dispara [setPerfil]; o ViewModel persiste no DataStore via
 * o repositório. Permissões por perfil ficam para as features (criar
 * projeto, atribuir tarefa, etc.) — esta tela só armazena a escolha.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repo: PerfilPreferencesRepository,
) : ViewModel() {

    /**
     * Estado de "salvando": true enquanto a chamada de DataStore está em
     * andamento. A UI desabilita os botões durante o save para evitar
     * duplo-clique.
     */
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    /**
     * Último erro ocorrido (caso o DataStore lance — improvável, mas
     * possível em devices com storage cheio). Null = sem erro pendente.
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Persiste o [p] no DataStore e expõe o estado de save.
     *
     * @param onSaved callback invocado quando a persistência termina com
     *                sucesso — usado pela UI para navegar para a home
     *                (`projetos`).
     */
    fun setPerfil(p: Perfil, onSaved: () -> Unit) {
        if (_saving.value) return // idempotente contra duplo-clique
        _saving.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repo.setPerfil(p)
                Log.i(TAG, "Perfil persistido: ${p.storageKey}")
                onSaved()
            } catch (t: Throwable) {
                Log.e(TAG, "Falha ao persistir perfil", t)
                _error.value = t.message ?: "Falha ao salvar perfil"
            } finally {
                _saving.value = false
            }
        }
    }

    /** Limpa a mensagem de erro atual (chamado pela UI após exibir). */
    fun clearError() {
        _error.value = null
    }

    private companion object {
        const val TAG = "OnboardingViewModel"
    }
}
