package com.joaopedrogms.brainoutapp.di

import com.joaopedrogms.brainoutapp.data.preferences.PerfilPreferencesRepository
import com.joaopedrogms.brainoutapp.data.preferences.PerfilPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para a camada de preferências locais.
 *
 * Responsabilidades:
 *  - Bind da interface [PerfilPreferencesRepository] para a impl
 *    [PerfilPreferencesRepositoryImpl] (DataStore Preferences).
 *
 * O `DataStore<Preferences>` em si é criado pelo delegate
 * `Context.preferencesDataStore` dentro do repositório (não precisa de
 * `@Provides` aqui — DataStore recomenda exatamente uma instância por
 * processo, e o delegate já garante isso via escopo do `Context.applicationContext`).
 *
 * Origem: ADR-0006 — perfil local opcional, persistido em DataStore.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindPerfilPreferencesRepository(
        impl: PerfilPreferencesRepositoryImpl,
    ): PerfilPreferencesRepository
}
