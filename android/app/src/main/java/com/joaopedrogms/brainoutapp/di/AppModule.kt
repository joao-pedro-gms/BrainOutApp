package com.joaopedrogms.brainoutapp.di

import android.content.Context
import androidx.room.Room
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.dao.TarefaDao
import com.joaopedrogms.brainoutapp.data.local.db.AppDatabase
import com.joaopedrogms.brainoutapp.data.repository.ProjetoRepositoryImpl
import com.joaopedrogms.brainoutapp.data.repository.TarefaRepositoryImpl
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
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
 *  - [AppDatabase] + DAOs (Room, ciclos 2 e 3);
 *  - bindings [ProjetoRepository] / [TarefaRepository] → suas `Impl`.
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
     * **Mudança desta lane (#10):** trocamos o
     * `fallbackToDestructiveMigration()` da lane de Projetos por uma
     * migration explícita ([AppDatabase.MIGRATION_1_2]). A justificativa
     * está no KDoc do `AppDatabase`: ainda não há release, então
     * "perder dados" não é um problema prático, mas é importante parar
     * de mascarar diferenças de schema — qualquer nova tabela passa por
     * uma migration real.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideProjetoDao(database: AppDatabase): ProjetoDao = database.projetoDao()

    @Provides
    fun provideTarefaDao(database: AppDatabase): TarefaDao = database.tarefaDao()
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

    @Binds
    @Singleton
    abstract fun bindTarefaRepository(
        impl: TarefaRepositoryImpl,
    ): TarefaRepository
}
