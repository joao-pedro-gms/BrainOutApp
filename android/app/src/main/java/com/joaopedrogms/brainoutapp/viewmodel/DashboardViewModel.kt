package com.joaopedrogms.brainoutapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.domain.repository.ProjetoRepository
import com.joaopedrogms.brainoutapp.domain.repository.TarefaRepository
import com.joaopedrogms.brainoutapp.ui.screens.dashboard.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel da tela **Dashboard** (issue #13).
 *
 * Combina os fluxos reativos de [ProjetoRepository] e [TarefaRepository]
 * para produzir um [DashboardUiState] com KPIs e séries para o gráfico.
 *
 *  - `atrasadas`: tarefas com `prazo < hoje` **e** status ≠ CONCLUIDA.
 *  - `concluidas` × `abertas`: contagens por status (CONCLUIDA vs resto).
 *  - `prazo7d`: tarefas com `hoje ≤ prazo ≤ hoje+7` (qualquer status).
 *  - `tarefasPorDia`: contagem por dia para os próximos 7 dias (hoje
 *    inclusive) — entrada do [SimpleBarChart][com.joaopedrogms.brainoutapp.ui.components.SimpleBarChart].
 *  - `proximasTarefasPrazo`: até 5 tarefas da janela `prazo7d`,
 *    ordenadas por prazo crescente.
 *
 * Erros nos flows de origem são capturados e convertidos em
 * [UiState.Error] (sem crash). O ponto de injeção (`@HiltViewModel`)
 * permite usar `hiltViewModel()` direto na tela — `MainActivity` já
 * tem `HiltAndroidApp` configurado em ciclos anteriores.
 *
 * O `Clock` é injetável para facilitar testes determinísticos de borda
 * (issue futura). No uso normal, vem o `Clock.systemDefaultZone()`.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    repoProjeto: ProjetoRepository,
    repoTarefa: TarefaRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {

    /**
     * Estado reativo da tela.
     *
     *  - Inicial: `UiState.Loading` (aguarda primeira emissão dos repos).
     *  - Sucesso: `UiState.Success(DashboardUiState(...))`.
     *  - Erro: `UiState.Error(mensagem)` se algum flow upstream falhar.
     */
    val state: StateFlow<UiState<DashboardUiState>> = combine(
        repoProjeto.getAll(),
        repoTarefa.getAll(),
    ) { projetos, tarefas ->
        val hoje = LocalDate.now(clock)
        UiState.Success(calcular(projetos = projetos, tarefas = tarefas, hoje = hoje))
            as UiState<DashboardUiState>
    }
        .onStart { Log.d(TAG, "DashboardViewModel: iniciando coleta de projetos+tarefas") }
        .catch { ex ->
            Log.e(TAG, "DashboardViewModel: erro no flow", ex)
            emit(UiState.Error(ex.message ?: "Erro ao carregar o dashboard"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    /**
     * Cálculo puro dos KPIs. `internal` para permitir testes diretos
     * (a task não exige testes nesta lane, mas a função é determinística
     * e barata — vale o gancho futuro).
     */
    internal fun calcular(
        projetos: List<com.joaopedrogms.brainoutapp.domain.model.Projeto>,
        tarefas: List<Tarefa>,
        hoje: LocalDate,
    ): DashboardUiState {
        val limite = hoje.plusDays(7)

        val atrasadas = tarefas.count { t ->
            t.prazo != null && t.prazo.isBefore(hoje) && t.status != StatusTarefa.CONCLUIDA
        }
        val concluidas = tarefas.count { it.status == StatusTarefa.CONCLUIDA }
        val abertas = tarefas.size - concluidas

        val naJanela = tarefas.filter { t ->
            t.prazo != null && !t.prazo.isBefore(hoje) && !t.prazo.isAfter(limite)
        }
        val prazo7d = naJanela.size

        // Série para o gráfico: contagem por dia nos próximos 7 dias.
        // Usa LocalDate (não Long) para clareza do consumidor.
        val contagemPorDia: Map<LocalDate, Int> = naJanela.groupingBy { it.prazo!! }.eachCount()
        val tarefasPorDia: List<Pair<LocalDate, Int>> = (0..6)
            .map { offset -> hoje.plusDays(offset.toLong()) }
            .map { dia -> dia to (contagemPorDia[dia] ?: 0) }

        val proximas = naJanela
            .sortedWith(compareBy({ it.prazo }, { it.prioridade.ordinal }))
            .take(5)

        return DashboardUiState(
            atrasadas = atrasadas,
            concluidas = concluidas,
            abertas = abertas,
            prazo7d = prazo7d,
            totalProjetos = projetos.size,
            totalTarefas = tarefas.size,
            tarefasPorDia = tarefasPorDia,
            proximasTarefasPrazo = proximas,
            hoje = hoje,
        )
    }

    private companion object {
        const val TAG = "DashboardVM"
    }
}
