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
 *        aberto). O FK é só o **contrato do banco**; a checagem semântica
 *        "tarefa aberta?" (RN02) mora no [com.joaopedrogms.brainoutapp
 *        .domain.usecase.ConcluirProjetoUseCase].
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
 *  - **`dependencias` (String, NOT NULL) — JSON array de UUIDs de tarefas
 *    que bloqueiam esta.** Implementa RN01 ("tarefa só pode ser concluída
 *    se todas as dependências estiverem concluídas"). Serializado como
 *    JSON textual — sem dependência de `kotlinx.serialization` ou
 *    `org.json`. Lista vazia → `"[]"`. Lista `["id1", "id2"]` →
 *    `'["id1","id2"]'`. Cuidar do escape de aspas duplas em títulos ou
 *    ids fora do padrão é responsabilidade de quem cria — nesta lane os
 *    ids sempre vêm do helper `UuidV7` (apenas hex + hífens) e portanto
 *    não precisam de escape.
 *
 * **Decisão desta lane (issue #11):**
 *  - O ER original previa um `dependente_id` (self-FK 1-para-1). Aqui
 *    optamos por uma lista (`dependencias: List<String>`) — múltiplas
 *    dependências por tarefa refletem melhor o workflow real ("tarefa B
 *    depende de A **e** C"). Trade-off: serialização como string JSON
 *    em vez de uma tabela associativa. Escolha consciente para manter o
 *    app 100% offline simples; queries "todas as tarefas que dependem
 *    de X" continuam possíveis via leitura em memória.
 *  - Migration v3 (`MIGRATION_2_3`) adiciona a coluna com DEFAULT `'[]'`
 *    para preencher linhas existentes.
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

    /**
     * Lista de ids de tarefas das quais **esta** depende, serializada
     * como JSON array de strings (ex.: `'["idA","idB"]'`). Vazio `[]` =
     * sem dependências. Default `[]` cobre a migração v3 sem necessidade
     * de backfill customizado.
     */
    @ColumnInfo(name = "dependencias", defaultValue = "[]")
    val dependencias: String = "[]",
)
