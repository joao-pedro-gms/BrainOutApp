package com.joaopedrogms.brainoutapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para a tabela `tarefas`.
 *
 * Reflete a tabela `tarefa` do ER (docs/modelo-dados.md):
 *  - PK `id` (String) — UUID v7 (ordenável por tempo).
 *    > Enquanto a lib `uuid-v7` não entra no catálogo, o `id` é gerado pelo
 *    > helper [com.joaopedrogms.brainoutapp.data.local.UuidV7] que por ora
 *    > delega para `java.util.UUID.randomUUID().toString()`. Nenhum caller
 *    > precisa mudar quando a lib entrar.
 *  - `projeto_id` (String, FK → `projetos.id`):
 *     - `onDelete = RESTRICT` (RN02 — não excluir projeto com tarefas em
 *        aberto). Esta lane **não** implementa RN02 (RN01-03 ficam para a
 *        lane de regras de negócio); o FK é só o **contrato do banco**.
 *     - `onUpdate = CASCADE` — se o `id` de um projeto mudar (improvável
 *        porque usamos UUID), a referência acompanha. Sem efeito até a
 *        lib uuid-v7 entrar.
 *  - `titulo` (String, NOT NULL) — validação ≤100 chars na UI / use case.
 *  - `descricao` (String, nullable) — opcional, ≤500 chars.
 *  - `prazo_millis` (Long, nullable) — epoch millis UTC (00:00 do dia em UTC).
 *    > Conversão para `LocalDate` acontece na camada de domínio; o banco
 *    > guarda `Long` para queries simples (mesmo padrão da `ProjetoEntity`).
 *  - `status` (String, NOT NULL) — enum persistido como `TEXT`.
 *    > Valores: `ABERTA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.
 *  - `prioridade` (String, NOT NULL) — enum `TEXT`.
 *    > Valores: `BAIXA`, `MEDIA`, `ALTA`, `URGENTE`.
 *  - `responsavel` (String, nullable) — nome livre ou id de perfil local;
 *    > app é single-user por dispositivo (ADR-0006), então o campo é
 *    > opcional e livre. Validação de tamanho/pertinência fica em use
 *    > cases posteriores.
 *  - `created_at` / `updated_at` (Long, NOT NULL) — epoch millis UTC.
 *  - `deleted_at` (Long, nullable) — soft delete. `null` = ativo.
 *
 * **Decisão desta lane (#10):**
 *  - O campo `dependente_id` (self-FK para RN01) está no ER mas **não**
 *    entra nesta entrega — RN01-03 ficam para a lane dedicada de regras
 *    de negócio. Quando entrar, será `nullable` e com
 *    `onDelete = SET_NULL` para não destruir a tarefa-mãe se a
 *    dependência for removida.
 *  - A constraint de FK garante que toda tarefa tem projeto válido. Se o
 *    usuário tentar excluir um projeto com tarefas (RN02), o SQLite joga
 *    `SQLiteConstraintException` — o repository / use case converterá em
 *    mensagem amigável em outra lane.
 *
 * Soft delete: o DAO filtra `WHERE deleted_at IS NULL` em todas as
 * queries de leitura, espelhando o padrão de `ProjetoEntity`.
 */
@Entity(
    tableName = "tarefas",
    foreignKeys = [
        ForeignKey(
            entity = ProjetoEntity::class,
            parentColumns = ["id"],
            childColumns = ["projeto_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("projeto_id")],
)
data class TarefaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "projeto_id")
    val projetoId: String,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "descricao")
    val descricao: String?,

    @ColumnInfo(name = "prazo_millis")
    val prazoMillis: Long?,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "prioridade")
    val prioridade: String,

    @ColumnInfo(name = "responsavel")
    val responsavel: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long?,
)
