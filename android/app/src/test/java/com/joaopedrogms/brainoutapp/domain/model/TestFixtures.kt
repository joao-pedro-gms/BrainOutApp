package com.joaopedrogms.brainoutapp.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Helpers para construir [Tarefa] e [Projeto] nos testes JUnit.
 *
 * Mantemos aqui (e não em `fakes/`) porque fixtures são parte da
 * superfície de teste do **domínio**, não do contrato de repositório.
 *
 * Convenções:
 *  - `id` é o primeiro parâmetro — testes geralmente precisam dele
 *    para asserções posteriores (`assertEquals(id, useCase(...).id)`).
 *  - Timestamps são fixos (`EPOCH`) — facilita asserts sobre
 *    `updatedAt` quando o teste quer provar que **não** mudou.
 *  - Defaults cobrem o "caso comum" — só sobrescreva o que importa.
 */
internal val EPOCH: Instant = Instant.parse("2024-01-01T00:00:00Z")

internal fun makeProjeto(
    id: String = "projeto-1",
    nome: String = "Projeto Teste",
    descricao: String? = null,
    prazo: LocalDate? = null,
    status: StatusProjeto = StatusProjeto.ABERTO,
    createdAt: Instant = EPOCH,
    updatedAt: Instant = EPOCH,
): Projeto = Projeto(
    id = id,
    nome = nome,
    descricao = descricao,
    prazo = prazo,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun makeTarefa(
    id: String = "tarefa-1",
    projetoId: String = "projeto-1",
    titulo: String = "Tarefa Teste",
    descricao: String? = null,
    prazo: LocalDate? = null,
    status: StatusTarefa = StatusTarefa.ABERTA,
    prioridade: PrioridadeTarefa = PrioridadeTarefa.BAIXA,
    responsavel: String? = null,
    createdAt: Instant = EPOCH,
    updatedAt: Instant = EPOCH,
    dependencias: List<String> = emptyList(),
): Tarefa = Tarefa(
    id = id,
    projetoId = projetoId,
    titulo = titulo,
    descricao = descricao,
    prazo = prazo,
    status = status,
    prioridade = prioridade,
    responsavel = responsavel,
    createdAt = createdAt,
    updatedAt = updatedAt,
    dependencias = dependencias,
)
