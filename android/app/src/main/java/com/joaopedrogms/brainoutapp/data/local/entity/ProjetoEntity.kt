package com.joaopedrogms.brainoutapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para a tabela `projetos`.
 *
 * Reflete a tabela `projeto` do ER (docs/modelo-dados.md):
 *  - PK `id` (String) — UUID v7 (ordenável por tempo).
 *    > **Pendência:** enquanto a lib `uuid-v7` não entra no catálogo, o `id` é
 *    > gerado pelo helper [com.joaopedrogms.brainoutapp.data.local.UuidV7]
 *    > que por ora delega para `java.util.UUID.randomUUID().toString()`.
 *    > Quando a lib uuid-v7 for adicionada em outra lane, basta trocar a
 *    > implementação do helper; nenhum caller precisa mudar.
 *  - `nome` (String, NOT NULL) — validação ≤100 chars fica na UI / use case.
 *  - `descricao` (String, nullable) — opcional, ≤500 chars.
 *  - `prazoMillis` (Long, nullable) — epoch millis UTC (00:00 do dia em UTC).
 *    > Conversão para `LocalDate` é feita na camada de domínio; o banco guarda
 *    > `Long` para evitar `TypeConverter` e simplificar queries.
 *  - `status` (String, NOT NULL) — enum persistido como `TEXT` (legibilidade).
 *    > Valores aceitos hoje: `ABERTO`, `CONCLUIDO`, `CANCELADO`.
 *    > O ER menciona "planejado|em_andamento|concluido|cancelado" — o ciclo
 *    > 3 (RN01-03) introduzirá `EM_ANDAMENTO`. Mantemos os 3 atuais até lá.
 *  - `createdAt` / `updatedAt` (Long, NOT NULL) — epoch millis UTC.
 *  - `deletedAt` (Long, nullable) — soft delete. `null` = ativo.
 *
 * Soft delete: o DAO filtra `WHERE deleted_at IS NULL` em todas as queries
 * de leitura para nunca expor linhas removidas à UI.
 */
@Entity(tableName = "projetos")
data class ProjetoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "nome")
    val nome: String,

    @ColumnInfo(name = "descricao")
    val descricao: String?,

    @ColumnInfo(name = "prazo_millis")
    val prazoMillis: Long?,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long?,
)