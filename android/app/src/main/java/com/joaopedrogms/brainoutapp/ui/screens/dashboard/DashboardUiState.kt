package com.joaopedrogms.brainoutapp.ui.screens.dashboard

import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import java.time.LocalDate

/**
 * Estado de domínio do Dashboard (issue #13).
 *
 * Diferente de [com.joaopedrogms.brainoutapp.viewmodel.UiState] — que
 * é apenas o wrapper Loading/Success/Error — esta classe carrega os
 * KPIs calculados a partir das listas reativas de projetos e tarefas:
 *
 *  - [atrasadas]         → tarefas com prazo < hoje e status ≠ CONCLUIDA.
 *  - [concluidas]        → tarefas com status = CONCLUIDA.
 *  - [abertas]           → tarefas com status ∈ {ABERTA, EM_ANDAMENTO,
 *                          CANCELADA} (i.e., tudo que **não** é CONCLUIDA).
 *  - [prazo7d]           → tarefas com prazo entre hoje e hoje+7 (inclusive).
 *  - [totalProjetos]     → contagem de projetos ativos.
 *  - [totalTarefas]      → contagem de tarefas ativas.
 *  - [tarefasPorDia]     → pares (epochDay, contagem) para os próximos 7
 *                          dias (hoje inclusive), usados pelo gráfico de
 *                          barras [com.joaopedrogms.brainoutapp.ui.components.SimpleBarChart].
 *  - [proximasTarefasPrazo] → até 5 tarefas com prazo dentro da janela
 *                          [hoje, hoje+7], ordenadas por prazo crescente.
 *                          Alimenta a lista compacta do card "Prazo 7 dias".
 *
 * > Decisão: `abertas` foi definido como `todas - concluidas` (ou seja,
 * > inclui CANCELADA). Isso bate com a interpretação prática de "tarefas
 * > ainda não concluídas" usada nos relatórios do protótipo. Caso o
 * > ciclo 3 prefira excluir CANCELADA, basta ajustar `calcular()` no
 * > [DashboardViewModel] — o tipo é estável.
 */
data class DashboardUiState(
    val atrasadas: Int = 0,
    val concluidas: Int = 0,
    val abertas: Int = 0,
    val prazo7d: Int = 0,
    val totalProjetos: Int = 0,
    val totalTarefas: Int = 0,
    val tarefasPorDia: List<Pair<LocalDate, Int>> = emptyList(),
    val proximasTarefasPrazo: List<Tarefa> = emptyList(),
    /** Dia-base (hoje) usado no cálculo de `tarefasPorDia` / `proximasTarefasPrazo`. */
    val hoje: LocalDate = LocalDate.now(),
)
