package com.joaopedrogms.brainoutapp.ui.screens.detalhes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusProjeto
import com.joaopedrogms.brainoutapp.ui.navigation.Destinations
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.ProjetoDetailViewModel
import com.joaopedrogms.brainoutapp.viewmodel.UiState
import kotlinx.coroutines.flow.collectLatest

/**
 * Tela 3 — Detalhes do projeto (wireframe 03-projeto-detalhe.svg).
 *
 * Versão **real** (issue #8): observa o [ProjetoDetailViewModel] (que por
 * sua vez consome o repository reativo) e mostra:
 *  - **Loading**: spinner.
 *  - **Não encontrado**: mensagem + botão voltar.
 *  - **Encontrado**: nome, descrição, prazo, status, datas; ações **Editar**
 *    (vai para `criacao/{projetoId}` — helper `Destinations.criacaoProjeto`)
 *    e **Excluir** (soft delete + navegação de volta via `Event.ProjetoExcluido`).
 *
 * O `id` é lido da rota via [Destinations.DETALHES_PROJETO_ARG] pelo
 * `SavedStateHandle` do VM. Mantemos `projetoId` no Composable para o
 * `Preview` e para tornar a chamada explícita na rota.
 *
 * > **Pendência (registrada em `RESULTADO.md`):** o NavHost atual não passa
 * > o callback `onEditar` para esta tela — o botão fica desabilitado até a
 * > lane de navegação conectar a rota `criacao/{projetoId}` e plugar o
 * > callback. Esta lane (#8) **não** mexe no NavHost por convenção.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    projetoId: String,
    onVoltar: () -> Unit = {},
    onEditar: (String) -> Unit = {},
    viewModel: ProjetoDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mostrarDialogoExcluir by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { evt ->
            when (evt) {
                ProjetoDetailViewModel.Event.ProjetoExcluido -> onVoltar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do projeto") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    (state as? UiState.Success)?.data?.let { projeto ->
                        IconButton(
                            onClick = { onEditar(projeto.id) },
                            // Habilita o botão apenas quando a rota-pai passou um
                            // callback não-default. No NavHost atual, o callback
                            // chega como `{}` e o botão fica desabilitado.
                            enabled = onEditar !== EMPTY_ON_EDITAR,
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar projeto")
                        }
                        IconButton(onClick = { mostrarDialogoExcluir = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir projeto")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val s = state) {
            is UiState.Loading -> Carregando(innerPadding)
            is UiState.Error -> TelaErro(innerPadding, s.message, onVoltar)
            is UiState.Success -> Conteudo(
                innerPadding = innerPadding,
                projeto = s.data,
                onEditar = onEditar,
            )
        }
    }

    if (mostrarDialogoExcluir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExcluir = false },
            title = { Text("Excluir projeto?") },
            text = {
                Text(
                    "Esta ação não pode ser desfeita (o projeto sai da lista, mas o " +
                        "histórico local é preservado pelo soft delete).",
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoExcluir = false
                        viewModel.excluir()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoExcluir = false }) { Text("Cancelar") }
            },
        )
    }
}

/** Sentinel para detectar se o caller passou o callback de editar ou deixou o default. */
private val EMPTY_ON_EDITAR: (String) -> Unit = {}

@Composable
private fun Carregando(innerPadding: PaddingValues) {
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
private fun TelaErro(innerPadding: PaddingValues, mensagem: String, onVoltar: () -> Unit) {
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
            text = "Erro ao carregar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(text = mensagem, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = onVoltar, modifier = Modifier.padding(top = 16.dp)) {
            Text("Voltar")
        }
    }
}

@Composable
private fun Conteudo(
    innerPadding: PaddingValues,
    projeto: Projeto?,
    onEditar: (String) -> Unit,
) {
    if (projeto == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Projeto não encontrado",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Ele pode ter sido excluído.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = projeto.nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(status = projeto.status)
        }

        projeto.descricao?.takeIf { it.isNotBlank() }?.let { desc ->
            Text(
                text = "Descrição",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(text = desc, style = MaterialTheme.typography.bodyMedium)
        }

        Text(
            text = "Prazo",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = projeto.prazo?.format(BRASIL_DATA_FORMAT) ?: "Sem prazo definido",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = "Histórico",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = "Criado em: ${projeto.createdAt.atZone(java.time.ZoneId.systemDefault()).format(BRASIL_DATA_HORA_FORMAT)}",
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "Atualizado em: ${projeto.updatedAt.atZone(java.time.ZoneId.systemDefault()).format(BRASIL_DATA_HORA_FORMAT)}",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = { onEditar(projeto.id) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = onEditar !== EMPTY_ON_EDITAR,
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Text("  Editar projeto")
        }
    }
}

@Composable
private fun StatusBadge(status: StatusProjeto) {
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
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

private val BRASIL_DATA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale("pt", "BR"))

private val BRASIL_DATA_HORA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR"))

@Preview(showBackground = true)
@Composable
private fun DetalhesScreenPreview() {
    BrainOutAppTheme {
        DetalhesScreen(projetoId = "preview-id")
    }
}