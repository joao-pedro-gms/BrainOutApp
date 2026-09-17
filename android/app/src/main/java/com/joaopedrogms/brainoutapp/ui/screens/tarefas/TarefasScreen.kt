package com.joaopedrogms.brainoutapp.ui.screens.tarefas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.domain.model.Tarefa
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.TarefaListViewModel
import com.joaopedrogms.brainoutapp.viewmodel.UiState

/**
 * Tela 4 — Lista global de tarefas com filtros (wireframe 04-tarefas.svg).
 *
 * Versão **real** (issue #10 — CRUD Tarefas):
 *  - Observa [TarefaListViewModel] (Room via use case) e renderiza:
 *     - **Loading** → `CircularProgressIndicator`.
 *     - **Empty state** → ícone + texto amigável + CTA "Criar primeira tarefa".
 *     - **Lista** → `LazyColumn` de `Card` com título, projeto (id curto),
 *       prazo, status e prioridade (chips).
 *     - **Erro** → texto + ícone `Inbox` vermelho.
 *  - **Filtros por status**: chips no topo (Todas / Aberta / Em andamento /
 *    Concluída / Cancelada). Aplicação é local no ViewModel.
 *  - **FAB** de criar tarefa — chama `onNovaTarefa`.
 *
 * Sem ações de edição / exclusão aqui — clicar em uma tarefa abre
 * `onAbrirTarefa(id)`, que navega para o form (a `TarefaFormScreen` é
 * compartilhada entre criar e editar; a edição pré-popula via `tarefaId`
 * na rota).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefasScreen(
    onNovaTarefa: () -> Unit = {},
    onAbrirTarefa: (String) -> Unit = {},
    onVoltar: () -> Unit = {},
    viewModel: TarefaListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filtro by viewModel.filtroStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tarefas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNovaTarefa) {
                Icon(Icons.Filled.Add, contentDescription = "Nova tarefa")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            FiltrosStatus(
                filtroAtual = filtro,
                onFiltroChange = viewModel::atualizarFiltroStatus,
            )
            when (val s = state) {
                is UiState.Loading -> TelaCarregando()
                is UiState.Error -> TelaErro(s.message)
                is UiState.Success -> TelaListaTarefas(
                    tarefas = s.data,
                    onAbrirTarefa = onAbrirTarefa,
                    onNovaTarefa = onNovaTarefa,
                )
            }
        }
    }
}

// =====================================================================================
// Subestados da tela
// =====================================================================================

@Composable
private fun TelaCarregando() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TelaErro(mensagem: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Inbox,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = "Erro ao carregar tarefas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(text = mensagem, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltrosStatus(
    filtroAtual: StatusTarefa?,
    onFiltroChange: (StatusTarefa?) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = filtroAtual == null,
                onClick = { onFiltroChange(null) },
                label = { Text("Todas") },
            )
        }
        items(StatusTarefa.entries.toList()) { status ->
            FilterChip(
                selected = filtroAtual == status,
                onClick = { onFiltroChange(status) },
                label = { Text(labelStatus(status)) },
                colors = FilterChipDefaults.filterChipColors(),
            )
        }
    }
}

@Composable
private fun TelaListaTarefas(
    tarefas: List<Tarefa>,
    onAbrirTarefa: (String) -> Unit,
    onNovaTarefa: () -> Unit,
) {
    if (tarefas.isEmpty()) {
        EmptyStateTarefas(onCriar = onNovaTarefa)
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = tarefas, key = { it.id }) { tarefa ->
            TarefaCard(
                tarefa = tarefa,
                onClick = { onAbrirTarefa(tarefa.id) },
            )
        }
    }
}

@Composable
private fun EmptyStateTarefas(onCriar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Assignment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Nenhuma tarefa ainda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Crie sua primeira tarefa para acompanhar o progresso.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        FloatingActionButton(
            onClick = onCriar,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Criar primeira tarefa")
        }
    }
}

@Composable
private fun TarefaCard(tarefa: Tarefa, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = tarefa.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                PrioridadeChip(prioridade = tarefa.prioridade)
            }

            tarefa.descricao?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChip(status = tarefa.status)
                tarefa.prazo?.let { prazo ->
                    Text(
                        text = "Prazo: ${prazo.format(BRASIL_DATA_FORMAT)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: StatusTarefa) {
    val (label, container) = when (status) {
        StatusTarefa.ABERTA -> "Aberta" to MaterialTheme.colorScheme.primaryContainer
        StatusTarefa.EM_ANDAMENTO -> "Em andamento" to MaterialTheme.colorScheme.secondaryContainer
        StatusTarefa.CONCLUIDA -> "Concluída" to MaterialTheme.colorScheme.tertiaryContainer
        StatusTarefa.CANCELADA -> "Cancelada" to MaterialTheme.colorScheme.errorContainer
    }
    Surface(
        color = container,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun PrioridadeChip(prioridade: PrioridadeTarefa) {
    val (label, container) = when (prioridade) {
        PrioridadeTarefa.BAIXA -> "Baixa" to MaterialTheme.colorScheme.surfaceVariant
        PrioridadeTarefa.MEDIA -> "Média" to MaterialTheme.colorScheme.secondaryContainer
        PrioridadeTarefa.ALTA -> "Alta" to MaterialTheme.colorScheme.tertiaryContainer
        PrioridadeTarefa.URGENTE -> "Urgente" to MaterialTheme.colorScheme.errorContainer
    }
    Surface(
        color = container,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun labelStatus(status: StatusTarefa): String = when (status) {
    StatusTarefa.ABERTA -> "Abertas"
    StatusTarefa.EM_ANDAMENTO -> "Em andamento"
    StatusTarefa.CONCLUIDA -> "Concluídas"
    StatusTarefa.CANCELADA -> "Canceladas"
}

/** Formatador compartilhado de datas em pt-BR (dd/MM/yyyy). */
private val BRASIL_DATA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale("pt", "BR"))

// =====================================================================================
// Previews
// =====================================================================================

@Preview(showBackground = true)
@Composable
private fun TarefasScreenEmptyPreview() {
    BrainOutAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Filled.Assignment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Nenhuma tarefa ainda", style = MaterialTheme.typography.titleLarge)
            Text("Crie sua primeira tarefa para acompanhar o progresso.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusChipPreview() {
    BrainOutAppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(StatusTarefa.ABERTA)
            StatusChip(StatusTarefa.EM_ANDAMENTO)
            StatusChip(StatusTarefa.CONCLUIDA)
            StatusChip(StatusTarefa.CANCELADA)
        }
    }
}
