package com.joaopedrogms.brainoutapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.joaopedrogms.brainoutapp.data.security.SecurityPreferencesRepository
import com.joaopedrogms.brainoutapp.data.security.SecurityPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para a camada de segurança (AppLock).
 *
 * Fornece:
 *  - [MasterKey] AES256_GCM (gerenciado pelo AndroidKeyStore);
 *  - [SharedPreferences] cifrada via [EncryptedSharedPreferences];
 *  - Binding [SecurityPreferencesRepository] → [SecurityPreferencesRepositoryImpl].
 *
 * A `EncryptedSharedPreferences` é exposta como `SharedPreferences` (não
 * como tipo concreto) porque (a) a API já permite isso e (b) isola o
 * tipo da `androidx.security` da camada de domínio — facilita testes
 * com fake.
 *
 * Origem: ADR-0006 — AppLock opcional, hash PBKDF2 persistido em prefs
 * cifradas.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    /**
     * Chave mestra AES256_GCM. O alias é o default
     * `_androidx_security_master_key_` (encapsulado pela lib).
     */
    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    /**
     * `EncryptedSharedPreferences` cifra tanto a **chave** (SIV) quanto o
     * **valor** (GCM) antes de tocar disco. Ver
     * `SecurityPreferencesRepositoryImpl` para detalhes.
     */
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKey: MasterKey,
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private const val PREFS_FILE_NAME = "brainoutapp_secure_prefs"
}

/**
 * Binding de contrato → implementação. Arquivo separado do
 * `object SecurityModule` porque o Hilt exige módulos `@Binds` como
 * `abstract class` (não é possível misturar `@Provides` e `@Binds` no
 * mesmo `Module`).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSecurityPreferencesRepository(
        impl: SecurityPreferencesRepositoryImpl,
    ): SecurityPreferencesRepository
}
