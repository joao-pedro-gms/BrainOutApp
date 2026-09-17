package com.joaopedrogms.brainoutapp.domain.model

import com.joaopedrogms.brainoutapp.data.local.entity.ProjetoEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Modelo de domínio do Projeto.
 *
 * Sem anotações Room — camada `domain` é pura (R12). As entidades Room
 * vivem em `data/local/entity/`, e os mappers abaixo traduzem entre as duas
 * representações. Isso permite:
 *  - trocar o storage sem tocar nas regras de negócio;
 *  - testar `domain/` sem dependência do Android framework.
 *
 * @property id          UUID v7 (ou v4 fallback — ver `UuidV7`).
 * @property nome        nome exibido na UI (obrigatório, ≤100 chars).
 * @property descricao   descrição opcional, ≤500 chars.
 * @property prazo       prazo opcional em `LocalDate` (UTC). `null` = sem prazo.
 *                       Validação de prazo ≥ hoje fica no `ProjetoFormViewModel`
 *                       e nos use cases do ciclo 3 (RN02/RN03).
 * @property status      estado atual. Padrão: [StatusProjeto.ABERTO].
 * @property createdAt   instante de criação (UTC).
 * @property updatedAt   instante da última alteração (UTC).
 *
 * > O domínio **não** carrega `deletedAt`: o repository filtra linhas
 * > soft-deleted no DAO, e a partir do boundary o domínio trabalha só com
 * * projetos ativos.
 */
data class Projeto(
    val id: String,
    val nome: String,
    val descricao: String?,
    val prazo: LocalDate?,
    val status: StatusProjeto,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/**
 * Estados possíveis do projeto.
 *
 * Mapeamento com o ER (docs/modelo-dados.md §PROJETO):
 *  - `ABERTO`     ↔ "planejado" (estado inicial ao criar)
 *  - `CONCLUIDO`  ↔ "concluido"  (ciclo 3: só se `RN02` passar)
 *  - `CANCELADO`  ↔ "cancelado"
 *
 * O valor `"em_andamento"` do ER será introduzido quando as regras de negócio
 * RN01-03 entrarem (ciclo 3). Por ora o app opera com os três acima.
 */
enum class StatusProjeto {
    ABERTO,
    CONCLUIDO,
    CANCELADO;

    companion object {
        /** Persistência tolerante: nunca quebra se vier um valor desconhecido. */
        fun fromStorage(value: String?): StatusProjeto =
            value?.let { name -> entries.firstOrNull { it.name == name } } ?: ABERTO
    }
}

// =====================================================================================
// Mappers entity ↔ domain
// =====================================================================================

/** `ProjetoEntity` (Room) → `Projeto` (domain). */
fun ProjetoEntity.toDomain(): Projeto = Projeto(
    id = id,
    nome = nome,
    descricao = descricao,
    prazo = prazoMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
    },
    status = StatusProjeto.fromStorage(status),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
)

/** `Projeto` (domain) → `ProjetoEntity` (Room). `deletedAt` sempre `null`. */
fun Projeto.toEntity(): ProjetoEntity = ProjetoEntity(
    id = id,
    nome = nome,
    descricao = descricao,
    prazoMillis = prazo?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
    status = status.name,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    deletedAt = null,
)