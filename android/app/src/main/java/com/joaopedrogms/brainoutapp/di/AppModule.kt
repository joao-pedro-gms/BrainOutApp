package com.joaopedrogms.brainoutapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo raiz do Hilt. Ciclos posteriores adicionarão módulos específicos
 * (DatabaseModule para Room, NetworkModule para Retrofit, RepositoryModule).
 *
 * Por enquanto fornecemos um OkHttpClient configurado para validar a
 * compilação do grafo de DI. Os endpoints reais serão plugados na issue
 * de integração com o backend FastAPI (issue #14).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}
