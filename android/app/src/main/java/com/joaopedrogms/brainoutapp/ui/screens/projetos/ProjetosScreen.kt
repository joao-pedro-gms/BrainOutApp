package com.joaopedrogms.brainoutapp.ui.screens.projetos

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.ProjetoListViewModel
import com.joaopedrogms.brainoutapp.viewmodel.UiState
import java.time.LocalDate

/**
 * Tela 2 — Lista de projetos (wireframe 02-projetos.svg).
 *
 * Versão **real** (issue #8): lê a lista reativa do [ProjetoListViewModel]
 * (Room) e renderiza:
 *  - **Loading** → `CircularProgressIndicator`.
 *  - **Empty state** → ícone + texto amigável + CTA "Criar primeiro projeto".
 *  - **Lista** → `LazyColumn` de `Card` com nome, descrição, prazo e status.
 *  - **Erro** → texto + botão "Tentar de novo" (placeholder — não há retry
 *    ainda; ciclo 3 pode adicionar `Snackbar` + retry).
 *
 * O `FloatingActionButton` (Plus) é a entrada primária de criação.
 * Mantemos os botões "Tarefas" e "Dashboard" como no stub — são destinos
 * paralelos no NavHost.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetosScreen(
    onAbrirProjeto: (String) -> Unit = {},
    onNovoProjeto: () -> Unit = {},
    onAbrirTarefas: () -> Unit = {},
    onAbrirDashboard: () -> Unit = {},
    viewModel: ProjetoListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Projetos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNovoProjeto) {
                Icon(Icons.Filled.Add, contentDescription = "Novo projeto")
            }
        },
    ) { innerPadding ->
        when (val s = state) {
            is UiState.Loading -> TelaCarregando(innerPadding)
            is UiState.Error -> TelaErro(innerPadding, s.message)
            is UiState.Success -> TelaListaProjetos(
                innerPadding = innerPadding,
                projetos = s.data,
                onAbrirProjeto = onAbrirProjeto,
                onAbrirTarefas = onAbrirTarefas,
                onAbrirDashboard = onAbrirDashboard,
            )
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
        CircularProgressIndicator()
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
            imageVector = Icons.Filled.Inbox,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = "Erro ao carregar projetos",
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
private fun TelaListaProjetos(
    innerPadding: PaddingValues,
    projetos: List<Projeto>,
    onAbrirProjeto: (String) -> Unit,
    onAbrirTarefas: () -> Unit,
    onAbrirDashboard: () -> Unit,
) {
    if (projetos.isEmpty()) {
        EmptyStateProjetos(
            innerPadding = innerPadding,
            onCriar = { onNovoProjeto() },
            onAbrirTarefas = onAbrirTarefas,
            onAbrirDashboard = onAbrirDashboard,
        )
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(onClick = onAbrirTarefas, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Inbox, contentDescription = null)
                Text("  Tarefas")
            }
            OutlinedButton(onClick = onAbrirDashboard, modifier = Modifier.weight(1f)) {
                Text("Dashboard")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = projetos, key = { it.id }) { projeto ->
                ProjetoCard(
                    projeto = projeto,
                    onClick = { onAbrirProjeto(projeto.id) },
                )
            }
        }
    }
}

@Composable
private fun EmptyStateProjetos(
    innerPadding: PaddingValues,
    onCriar: () -> Unit,
    onAbrirTarefas: () -> Unit,
    onAbrirDashboard: () -> Unit,
) {
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
            text = "Nenhum projeto ainda",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Crie seu primeiro projeto para começar a organizar tarefas.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        FloatingActionButton(
            onClick = onCriar,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Criar primeiro projeto")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(onClick = onAbrirTarefas, modifier = Modifier.weight(1f)) {
                Text("Tarefas")
            }
            OutlinedButton(onClick = onAbrirDashboard, modifier = Modifier.weight(1f)) {
                Text("Dashboard")
            }
        }
    }
}

@Composable
private fun ProjetoCard(projeto: Projeto, onClick: () -> Unit) {
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
                    text = projeto.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(status = projeto.status)
            }

            projeto.descricao?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 3,
                )
            }

            projeto.prazo?.let { prazo ->
                Text(
                    text = "Prazo: ${prazo.format(BRASIL_DATA_FORMAT)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: StatusProjeto) {
    val (label, container) = when (status) {
        StatusProjeto.ABERTO -> "Aberto" to MaterialTheme.colorScheme.primaryContainer
        StatusProjeto.CONCLUIDO -> "Concluído" to MaterialTheme.colorScheme.tertiaryContainer
        StatusProjeto.CANCELADO -> "Cancelado" to MaterialTheme.colorScheme.errorContainer
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

/** Formatador compartilhado de datas em pt-BR (dd/MM/yyyy). */
private val BRASIL_DATA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale("pt", "BR"))

// =====================================================================================
// Previews
// =====================================================================================

@Preview(showBackground = true)
@Composable
private fun ProjetosScreenEmptyPreview() {
    BrainOutAppTheme {
        // Pré-visualização estática — sem ViewModel real.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("Nenhum projeto ainda", style = MaterialTheme.typography.titleLarge)
            Text("Crie seu primeiro projeto para começar.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusChipPreview() {
    BrainOutAppTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(StatusProjeto.ABERTO)
            StatusChip(StatusProjeto.CONCLUIDO)
            StatusChip(StatusProjeto.CANCELADO)
        }
    }
}