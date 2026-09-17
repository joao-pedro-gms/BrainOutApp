package com.joaopedrogms.brainoutapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Perfis locais possíveis do usuário.
 *
 * Persistido em DataStore Preferences como `String` (nome do enum) para
 * sobreviver a refactors de ordem/adição de novos perfis sem corromper
 * dados antigos (a desserialização usa [Perfil.fromStorageKey] e cai em
 * `null` se vier um valor desconhecido).
 *
 * Origem: ADR-0006 — perfil local opcional, sem autenticação online.
 */
enum class Perfil(val storageKey: String) {
    GERENTE("GERENTE"),
    COLABORADOR("COLABORADOR"),
    ;

    companion object {
        /**
         * Desserializa um valor vindo do DataStore. Retorna `null` quando
         * o valor é ausente ou desconhecido — a UI então mantém o usuário
         * em onboarding (não há "default" de perfil: PRD exige escolha
         * explícita, R2/ADR-0006).
         */
        fun fromStorageKey(value: String?): Perfil? =
            value?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}

/**
 * Contrato do repositório de preferências de perfil.
 *
 * Implementações vivem em [PerfilPreferencesRepositoryImpl] e são
 * injetadas via Hilt (módulo `di/PreferencesModule.kt`).
 *
 * R12: a UI nunca deve consultar o DataStore diretamente — sempre
 * passa por esta abstração para que ViewModels/Use Cases possam
 * revalidar a permissão no domínio.
 */
interface PerfilPreferencesRepository {
    /** Retorna o perfil atual ou `null` se ainda não foi escolhido (onboarding pendente). */
    suspend fun getPerfil(): Perfil?

    /** Persiste o perfil escolhido pelo usuário (chamado pelo onboarding/Configurações). */
    suspend fun setPerfil(p: Perfil)
}

/**
 * Extensão de [Context] que cria/expõe o `DataStore<Preferences>` único
 * da aplicação (criado uma única vez por processo — ver docs DataStore).
 *
 * O nome do arquivo (`brainoutapp_prefs`) é estável entre versões e
 * lanes; mudar esse valor exigiria migração.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brainoutapp_prefs")

/**
 * Implementação default do [PerfilPreferencesRepository] usando DataStore Preferences.
 *
 * Chave: `perfil_usuario` (string com [Perfil.storageKey]).
 *
 * Não envolve try/catch amplo: DataStore é projetado para nunca lançar em
 * fluxo normal (erros viram IOException no `data.first()`, que propagamos
 * para a camada acima decidir fallback).
 */
@Singleton
class PerfilPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PerfilPreferencesRepository {

    override suspend fun getPerfil(): Perfil? {
        val stored = context.dataStore.data.first()[prefsKey]
        return Perfil.fromStorageKey(stored)
    }

    override suspend fun setPerfil(p: Perfil) {
        context.dataStore.edit { it[prefsKey] = p.storageKey }
    }

    private companion object {
        val prefsKey = stringPreferencesKey("perfil_usuario")
    }
}
