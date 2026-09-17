package com.joaopedrogms.brainoutapp.di

import android.content.Context
import androidx.room.Room
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.db.AppDatabase
import com.joaopedrogms.brainoutapp.data.repository.ProjetoRepositoryImpl
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo raiz do Hilt.
 *
 * Fornece:
 *  - [OkHttpClient] (placeholder, ciclo 1);
 *  - [AppDatabase] + DAOs (Room, ciclo 2 — issue #8 CRUD Projetos);
 *  - binding [ProjetoRepository] → [ProjetoRepositoryImpl] (ciclo 2).
 *
 * Módulos específicos (Network, etc.) podem ser adicionados em outros
 * arquivos `@InstallIn(SingletonComponent::class)` para manter este
 * enxuto.
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

    /**
     * Instância singleton do banco Room local.
     *
     * `fallbackToDestructiveMigration()` cobre o caso (improvável) de alguém
     * instalar o APK de desenvolvimento em cima de um banco de versão mais
     * antiga gerado por uma branch paralela — perdemos dados, mas o app
     * abre. Em release (ciclo 3+) removeremos e adicionaremos migrations
     * explícitas via `Room.databaseBuilder().addMigrations(...)`.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProjetoDao(database: AppDatabase): ProjetoDao = database.projetoDao()
}

/**
 * Módulo abstrato: bindings de contrato → implementação.
 *
 * Mantido em arquivo separado do `object AppModule` porque o Hilt exige
 * módulos `@Binds` como `abstract class` — não dá para misturar `@Provides`
 * (em `object`) com `@Binds` (em `abstract class`) no mesmo `Module`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjetoRepository(
        impl: ProjetoRepositoryImpl,
    ): ProjetoRepository
}