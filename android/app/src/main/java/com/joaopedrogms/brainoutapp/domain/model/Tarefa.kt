package com.joaopedrogms.brainoutapp.domain.model

import com.joaopedrogms.brainoutapp.data.local.entity.TarefaEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Modelo de domínio da Tarefa.
 *
 * Sem anotações Room — camada `domain` é pura (R12). As entidades Room
 * vivem em `data/local/entity/`, e os mappers abaixo traduzem entre as
 * duas representações.
 *
 * @property id          UUID v7 (ou v4 fallback — ver `UuidV7`).
 * @property projetoId   FK para [Projeto.id]. Obrigatório. Validação de
 *                       existência fica no repository (em ciclo posterior,
 *                       via [TarefaRepository.listarProjetos] — esta lane
 *                       só aceita `String` e o SQLite valida a FK).
 * @property titulo      título exibido na UI (obrigatório, ≤100 chars).
 * @property descricao   descrição opcional, ≤500 chars.
 * @property prazo       prazo opcional em `LocalDate` (UTC). `null` = sem prazo.
 *                       Validação de prazo ≥ hoje fica no `TarefaFormViewModel`;
 *                       a checagem contra prazo do projeto (RN03) é feita em
 *                       [com.joaopedrogms.brainoutapp.domain.usecase.CriarTarefaUseCase]
 *                       / [com.joaopedrogms.brainoutapp.domain.usecase.EditarTarefaUseCase].
 * @property status      estado atual. Padrão: [StatusTarefa.ABERTA].
 * @property prioridade  urgência. Padrão: [PrioridadeTarefa.BAIXA].
 * @property responsavel nome livre do responsável (opcional).
 * @property createdAt   instante de criação (UTC).
 * @property updatedAt   instante da última alteração (UTC).
 * @property dependencias ids de tarefas das quais **esta** depende — RN01
 *                       ("só pode concluir se todas as dependências
 *                       estiverem concluídas"). Lista vazia significa
 *                       "sem dependências"; nesta lane, dependências não
 *                       são auto-resolvidas (uma tarefa pode referenciar
 *                       outra que foi soft-deleted — o use case trata
 *                       como satisfeita).
 *
 * > O domínio **não** carrega `deletedAt`: o repository filtra linhas
 * > soft-deleted no DAO.
 */
data class Tarefa(
    val id: String,
    val projetoId: String,
    val titulo: String,
    val descricao: String?,
    val prazo: LocalDate?,
    val status: StatusTarefa,
    val prioridade: PrioridadeTarefa,
    val responsavel: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val dependencias: List<String> = emptyList(),
)

/**
 * Estados possíveis da tarefa.
 *
 * Mapeamento com o ER (docs/modelo-dados.md §TAREFA):
 *  - `ABERTA`         ↔ "aberta"           (estado inicial ao criar).
 *  - `EM_ANDAMENTO`   ↔ "em_andamento".
 *  - `CONCLUIDA`      ↔ "concluida" (RN01 — só com dependências ok).
 *  - `CANCELADA`      ↔ "cancelada".
 *
 * Esta enum já cobre o ciclo completo previsto no ER — diferentemente de
 * [StatusProjeto] (que omite `EM_ANDAMENTO` por causa do ciclo de entrega).
 * A conversão `fromStorage` é tolerante: nunca quebra se vier um valor
 * desconhecido vindo do banco (fallback para [ABERTA]).
 */
enum class StatusTarefa {
    ABERTA,
    EM_ANDAMENTO,
    CONCLUIDA,
    CANCELADA;

    companion object {
        /** Persistência tolerante: nunca quebra se vier um valor desconhecido. */
        fun fromStorage(value: String?): StatusTarefa =
            value?.let { name -> entries.firstOrNull { it.name == name } } ?: ABERTA
    }
}

/**
 * Prioridade da tarefa.
 *
 * Mapeamento com o ER (docs/modelo-dados.md §TAREFA):
 *  - `BAIXA`, `MEDIA`, `ALTA` ↔ "baixa", "media", "alta".
 *  - `URGENTE` é uma **extensão** local — não está no ER original mas é
 *    útil para a UX do filtro de tarefas; entra como valor válido no
 *    storage (sem quebrar leitura de bancos antigos, graças ao
 *    `fromStorage` tolerante).
 */
enum class PrioridadeTarefa {
    BAIXA,
    MEDIA,
    ALTA,
    URGENTE;

    companion object {
        /** Persistência tolerante: nunca quebra se vier um valor desconhecido. */
        fun fromStorage(value: String?): PrioridadeTarefa =
            value?.let { name -> entries.firstOrNull { it.name == name } } ?: BAIXA
    }
}

// =====================================================================================
// Mappers entity ↔ domain
// =====================================================================================

/** `TarefaEntity` (Room) → `Tarefa` (domain). */
fun TarefaEntity.toDomain(): Tarefa = Tarefa(
    id = id,
    projetoId = projetoId,
    titulo = titulo,
    descricao = descricao,
    prazo = prazoMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
    },
    status = StatusTarefa.fromStorage(status),
    prioridade = PrioridadeTarefa.fromStorage(prioridade),
    responsavel = responsavel,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    dependencias = decodeDependencias(dependencias),
)

/** `Tarefa` (domain) → `TarefaEntity` (Room). `deletedAt` sempre `null`. */
fun Tarefa.toEntity(): TarefaEntity = TarefaEntity(
    id = id,
    projetoId = projetoId,
    titulo = titulo,
    descricao = descricao,
    prazoMillis = prazo?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
    status = status.name,
    prioridade = prioridade.name,
    responsavel = responsavel,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    deletedAt = null,
    dependencias = encodeDependencias(dependencias),
)

// =====================================================================================
// Serialização JSON da lista de dependências (sem dependência externa)
// =====================================================================================

/**
 * Codifica `List<String>` como JSON array de strings.
 *
 * Formato: `'["idA","idB"]'`. Strings que precisem de escape (aspas
 * duplas, contrabarra, controle) são escapadas — embora nesta lane os
 * ids sempre venham do helper `UuidV7` (apenas hex + hífens), o escape
 * é defensivo caso o helper mude ou seja fornecido por terceiros.
 *
 * Lista vazia → `"[]"`. Lista `null` (improvável — o domínio tem
 * default `emptyList()`) também → `"[]"` por tolerância.
 */
internal fun encodeDependencias(valor: List<String>?): String {
    val lista = valor ?: emptyList()
    if (lista.isEmpty()) return "[]"
    return lista.joinToString(
        prefix = "[",
        postfix = "]",
        separator = ",",
    ) { "\"" + escapeJsonString(it) + "\"" }
}

/**
 * Decodifica uma string JSON array de strings em `List<String>`.
 *
 * Tolerante: devolve `emptyList()` se o input for vazio, `"[]"`, `null`,
 * ou estiver malformado. Em caso de malformado, **loga** (via
 * `android.util.Log`) — ainda não há logger estruturado nesta lane;
 * usar Logcat direto é consistente com o resto do `data/`.
 */
internal fun decodeDependencias(raw: String?): List<String> {
    if (raw.isNullOrBlank() || raw == "[]") return emptyList()
    val trimmed = raw.trim()
    if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
        android.util.Log.w("TarefaMapper", "decodeDependencias: JSON malformado, retornando []. raw=$raw")
        return emptyList()
    }
    val inner = trimmed.substring(1, trimmed.length - 1).trim()
    if (inner.isEmpty()) return emptyList()
    // Split manual respeitando aspas — bom o bastante para esta lane
    // (ids sem aspas/contrabarra). Parsing JSON completo viria com
    // kotlinx.serialization ou org.json.
    return inner.split(',').mapNotNull { item ->
        val semAspas = item.trim().removeSurrounding("\"")
        if (semAspas.isEmpty()) null else semAspas
    }
}

/** Escape mínimo para JSON strings (aspas duplas + contrabarra + controles). */
private fun escapeJsonString(value: String): String {
    val sb = StringBuilder(value.length + 2)
    for (c in value) {
        when (c) {
            '\\' -> sb.append("\\\\")
            '"' -> sb.append("\\\"")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else -> if (c.code < 0x20) {
                sb.append("\\u").append("%04x".format(c.code))
            } else {
                sb.append(c)
            }
        }
    }
    return sb.toString()
}
