package com.joaopedrogms.brainoutapp.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.ui.components.SimpleBarChart
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.DashboardViewModel
import com.joaopedrogms.brainoutapp.viewmodel.UiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Tela 4 — Dashboard (wireframe 06-dashboard.svg, issue #13).
 *
 * Versão **real**:
 *  - Lê o [DashboardViewModel] (Hilt) e reage aos fluxos de projetos
 *    e tarefas para montar os KPIs.
 *  - **Loading** → spinner centralizado.
 *  - **Erro** → mensagem amigável + botão "Tentar de novo" (placeholder).
 *  - **Empty** (sem projetos) → ícone + CTA "Crie seu primeiro projeto…".
 *  - **Sucesso** → header com data, três cards (Atrasadas, Concluídas vs
 *    Abertas, Prazo 7 dias) + [SimpleBarChart] + footer com totais.
 *
 * Cores e tipografia vêm do design system (Material 3 + tokens
 * semânticos em `ui/theme/Color.kt`); nada de hex literal nesta tela.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVoltar: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val s = state) {
            is UiState.Loading -> TelaCarregando(innerPadding)
            is UiState.Error -> TelaErro(innerPadding, s.message)
            is UiState.Success -> {
                if (s.data.totalProjetos == 0) {
                    EmptyStateDashboard(innerPadding)
                } else {
                    TelaDashboard(innerPadding, s.data)
                }
            }
        }
    }
}

// =====================================================================================
// Subestados da tela
// =====================================================================================

@Composable
private fun TelaCarregando(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun TelaErro(innerPadding: PaddingValues, mensagem: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Dashboard,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = "Erro ao carregar o dashboard",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun EmptyStateDashboard(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Folder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Sem dados ainda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Crie seu primeiro projeto para ver o dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun TelaDashboard(
    innerPadding: PaddingValues,
    data: DashboardUiState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HeaderDashboard(hoje = data.hoje, totalProjetos = data.totalProjetos)
        }
        item {
            CardAtrasadas(atrasadas = data.atrasadas)
        }
        item {
            CardConcluidasVsAbertas(concluidas = data.concluidas, abertas = data.abertas)
        }
        item {
            CardPrazo7Dias(
                prazo7d = data.prazo7d,
                tarefas = data.proximasTarefasPrazo,
            )
        }
        item {
            CardGrafico(data = data)
        }
        item {
            FooterStats(totalProjetos = data.totalProjetos, totalTarefas = data.totalTarefas)
        }
    }
}

// =====================================================================================
// Peças: header, cards, footer
// =====================================================================================

@Composable
private fun HeaderDashboard(hoje: LocalDate, totalProjetos: Int) {
    val titulo = hoje.format(DATE_FORMAT_HEADER)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "BrainOutApp",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(
            text = "Visão geral de $totalProjetos projeto(s)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Card 1 — Atrasadas: número grande em vermelho + subtítulo. */
@Composable
private fun CardAtrasadas(atrasadas: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Atrasadas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = atrasadas.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = if (atrasadas == 1) "tarefa com prazo vencido"
                else "tarefas com prazo vencido",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** Card 2 — Concluídas vs Abertas: "X / Y" + barra de progresso. */
@Composable
private fun CardConcluidasVsAbertas(concluidas: Int, abertas: Int) {
    val total = (concluidas + abertas).coerceAtLeast(1)
    val ratio = concluidas.toFloat() / total.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Concluídas vs Abertas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = concluidas.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "/ $abertas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinearProgressIndicator(
                progress = { ratio.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
            )
            Text(
                text = "${(ratio * 100).toInt()}% concluído",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

/** Card 3 — Prazo 7 dias: número + lista compacta das próximas 5 tarefas. */
@Composable
private fun CardPrazo7Dias(prazo7d: Int, tarefas: List<Tarefa>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Prazo 7 dias",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = prazo7d.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = if (prazo7d == 1) "tarefa vence nos próximos 7 dias"
                else "tarefas vencem nos próximos 7 dias",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            if (tarefas.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    tarefas.forEach { t ->
                        LinhaTarefaPrazo(t)
                    }
                }
            }
        }
    }
}

@Composable
private fun LinhaTarefaPrazo(t: Tarefa) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = t.prazo?.format(BRASIL_DATA_FORMAT).orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = t.titulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            maxLines = 1,
        )
    }
}

/** Card do gráfico de barras (próximos 7 dias). */
@Composable
private fun CardGrafico(data: DashboardUiState) {
    val labels = data.tarefasPorDia.map { (dia, _) ->
        // Primeira letra do dia da semana em pt-BR (S T Q Q S S D).
        dia.dayOfWeek
            .getDisplayName(TextStyle.NARROW, Locale("pt", "BR"))
            .uppercase(Locale("pt", "BR"))
    }
    val counts = data.tarefasPorDia.map { it.second }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tarefas por dia (próximos 7)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SimpleBarChart(
                data = labels.zip(counts),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun FooterStats(totalProjetos: Int, totalTarefas: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatBloco(label = "Projetos", valor = totalProjetos)
        StatBloco(label = "Tarefas", valor = totalTarefas)
    }
}

@Composable
private fun StatBloco(label: String, valor: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// =====================================================================================
// Formatadores
// =====================================================================================

/** "Quinta-feira, 17 de setembro de 2026" (pt-BR). */
private val DATE_FORMAT_HEADER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

private val BRASIL_DATA_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR"))

// =====================================================================================
// Previews
// =====================================================================================

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    BrainOutAppTheme {
        TelaDashboard(
            innerPadding = PaddingValues(0.dp),
            data = DashboardUiState(
                atrasadas = 3,
                concluidas = 7,
                abertas = 5,
                prazo7d = 4,
                totalProjetos = 2,
                totalTarefas = 12,
                tarefasPorDia = listOf(
                    LocalDate.now() to 1,
                    LocalDate.now().plusDays(1) to 2,
                    LocalDate.now().plusDays(2) to 0,
                    LocalDate.now().plusDays(3) to 3,
                    LocalDate.now().plusDays(4) to 1,
                    LocalDate.now().plusDays(5) to 0,
                    LocalDate.now().plusDays(6) to 2,
                ),
                proximasTarefasPrazo = listOf(
                    Tarefa(
                        id = "1",
                        projetoId = "p1",
                        titulo = "Revisar PR de autenticação",
                        descricao = null,
                        prazo = LocalDate.now().plusDays(1),
                        status = StatusTarefa.EM_ANDAMENTO,
                        prioridade = PrioridadeTarefa.ALTA,
                        responsavel = "Ana",
                        createdAt = java.time.Instant.now(),
                        updatedAt = java.time.Instant.now(),
                    ),
                    Tarefa(
                        id = "2",
                        projetoId = "p1",
                        titulo = "Atualizar README",
                        descricao = null,
                        prazo = LocalDate.now().plusDays(3),
                        status = StatusTarefa.ABERTA,
                        prioridade = PrioridadeTarefa.MEDIA,
                        responsavel = null,
                        createdAt = java.time.Instant.now(),
                        updatedAt = java.time.Instant.now(),
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardEmptyPreview() {
    BrainOutAppTheme {
        EmptyStateDashboard(innerPadding = PaddingValues(0.dp))
    }
}
