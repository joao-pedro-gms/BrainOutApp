package com.joaopedrogms.brainoutapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.joaopedrogms.brainoutapp.data.local.dao.ProjetoDao
import com.joaopedrogms.brainoutapp.data.local.dao.TarefaDao
import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity
import com.joaopedrogms.brainoutapp.data.local.entity.TarefaEntity

/**
 * Banco Room local do BrainOutApp.
 *
 * Histórico de versões:
 *  - v1 (issue #8 — CRUD Projetos): apenas tabela `projetos`.
 *  - v2 (issue #10 — CRUD Tarefas): adiciona tabela `tarefas` com
 *    FK `RESTRICT` para `projetos(id)` (RN02). Migration [MIGRATION_1_2]
 *    recria a tabela sem perda — segura porque o banco ainda não tem
 *    dados de produção (release é posterior a esta lane).
 *  - **v3 (issue #11 — RN01-RN03):** adiciona coluna `dependencias` (TEXT
 *    JSON) à tabela `tarefas`. Migration [MIGRATION_2_3] faz `ALTER TABLE`
 *    com `DEFAULT '[]'`, preenchendo retroativamente todas as linhas
 *    existentes com lista vazia (sem dependências).
 *
 * **Decisão sobre `fallbackToDestructiveMigration()`:** o `AppModule`
 * **removeu** o fallback destrutivo na lane #10. Em release seguimos o
 * contrato de migration; em debug, se uma branch paralela instalar APK
 * com versão de schema diferente sem migration, o Room lançará
 * `IllegalStateException` (mais seguro para detectar drift).
 *
 * `exportSchema = false` continua por enquanto (ciclo 3 liga `true`
 * quando o CI de schema entrar).
 */
@Database(
    entities = [ProjetoEntity::class, TarefaEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    /** DAO da tabela `projetos`. Fornecido via Hilt em `di/AppModule.kt`. */
    abstract fun projetoDao(): ProjetoDao

    /** DAO da tabela `tarefas`. Fornecido via Hilt em `di/AppModule.kt`. */
    abstract fun tarefaDao(): TarefaDao

    companion object {
        /** Nome do arquivo SQLite no diretório padrão do app (`databases/`). */
        const val DATABASE_NAME = "brainoutapp.db"

        /**
         * Migration v1 → v2: cria a tabela `tarefas` com FK `RESTRICT`
         * para `projetos(id)` e índice em `projeto_id`.
         *
         > **Por que CREATE TABLE direto e não `ALTER TABLE`?** a v1 só
         > tinha `projetos`; não há transformação a aplicar — basta
         > adicionar a nova tabela. A FK é criada inline para evitar
         > problemas de ordem de criação (a tabela `projetos` já existe
         > desde a v1, então a referência é válida).
         */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tarefas` (
                        `id` TEXT NOT NULL,
                        `projeto_id` TEXT NOT NULL,
                        `titulo` TEXT NOT NULL,
                        `descricao` TEXT,
                        `prazo_millis` INTEGER,
                        `status` TEXT NOT NULL,
                        `prioridade` TEXT NOT NULL,
                        `responsavel` TEXT,
                        `created_at` INTEGER NOT NULL,
                        `updated_at` INTEGER NOT NULL,
                        `deleted_at` INTEGER,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`projeto_id`) REFERENCES `projetos`(`id`)
                            ON UPDATE CASCADE ON DELETE RESTRICT
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tarefas_projeto_id` ON `tarefas` (`projeto_id`)")
            }
        }

        /**
         * Migration v2 → v3 (issue #11 — RN01-RN03): adiciona a coluna
         * `dependencias` (TEXT, default `'[]'`) à tabela `tarefas`.
         *
         * Sem perda de dados: linhas existentes ganham lista vazia como
         * default, ou seja, passam a se comportar como tarefas sem
         * dependências (regra mais permissiva — RN01 não bloqueia nada
         * retroativamente). Se houver tarefas que dependiam umas das
         * outras sem o campo, o usuário precisa recadastrar via UI
         * (campo "dependências" adicionado em `TarefaFormScreen`).
         *
         * > Por que `ALTER TABLE … DEFAULT '[]'` e não backfill via
         * > `UPDATE`? O default aplica tanto a linhas existentes quanto
         * a novas, eliminando duas operações e ficando atômico.
         */
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `tarefas` ADD COLUMN `dependencias` TEXT NOT NULL DEFAULT '[]'",
                )
            }
        }
    }
}
