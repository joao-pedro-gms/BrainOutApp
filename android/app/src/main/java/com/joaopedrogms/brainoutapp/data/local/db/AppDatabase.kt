package com.joaopedrogms.brainoutapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity

/**
 * Banco Room local do BrainOutApp (issue #8 — CRUD Projetos).
 *
 *  - `version = 1` — schema inicial contém apenas a tabela `projetos`.
 *  - `exportSchema = true` declarado no `AppModule` (não aqui, mas a anotação
 *    `@Database` ativa a geração do schema JSON em
 *    `app/schemas/`).
 *  - `TarefaEntity` será adicionada em ciclo futuro (issue da RN01-03);
 *    o bump de versão e a migration correspondente virão juntos.
 *
 * Por enquanto não há `TypeConverters` — datas ficam como `Long` (epoch
 * millis) na entity e a conversão para `LocalDate` acontece na camada
 * de domínio. Isso mantém o banco simples e evita conversão inútil em
 * queries que filtram por `prazo_millis`.
 */
@Database(
    entities = [ProjetoEntity::class],
    version = 1,
    exportSchema = false, // ciclo 2: simplificado (sem CI de schema); ciclo 3 liga `true` quando TarefaEntity entrar.
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO da tabela `projetos`. Fornecido via Hilt em `di/AppModule.kt`. */
    abstract fun projetoDao(): ProjetoDao

    companion object {
        /** Nome do arquivo SQLite no diretório padrão do app (`databases/`). */
        const val DATABASE_NAME = "brainoutapp.db"
    }
}