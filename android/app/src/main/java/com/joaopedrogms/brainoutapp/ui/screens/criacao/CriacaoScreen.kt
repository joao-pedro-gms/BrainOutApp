package com.joaopedrogms.brainoutapp.ui.screens.criacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.ProjetoFormViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Tela 5 — Formulário de projeto (criar e editar).
 *
 * Roteamento: aceita o argumento `projetoId` opcional da rota. Quando
 * preenchido, o [ProjetoFormViewModel] entra em modo "edição" e
 * pré-popula os campos a partir do banco local.
 *
 * Comportamento:
 *  - **Carregando**: spinner.
 *  - **Pronto**: form com nome / descrição / prazo (DatePicker) / status.
 *    Cada campo dispara validação on-the-fly (mensagens abaixo do input).
 *  - **Submetendo**: botões desabilitados, "Salvando…".
 *  - **Erro de validação**: campo com `isError = true` + mensagem.
 *  - **Erro geral**: texto abaixo do form.
 *  - **Concluído** (one-shot): volta para a tela anterior.
 *
 * Validações (issue #8):
 *  - nome: obrigatório, ≤ 100.
 *  - descrição: ≤ 500.
 *  - prazo: se preenchido, ≥ hoje.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriacaoScreen(
    onVoltar: () -> Unit = {},
    viewModel: ProjetoFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { evt ->
            when (evt) {
                ProjetoFormViewModel.Event.Concluido -> onVoltar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.ehEdicao) "Editar projeto" else "Criar projeto") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.carregando -> Carregando(innerPadding)
            else -> Formulario(
                innerPadding = innerPadding,
                state = state,
                onNomeChange = viewModel::onNomeChange,
                onDescricaoChange = viewModel::onDescricaoChange,
                onPrazoChange = viewModel::onPrazoChange,
                onSalvar = viewModel::salvar,
                onCancelar = onVoltar,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Formulario(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    state: ProjetoFormViewModel.FormState,
    onNomeChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onPrazoChange: (LocalDate?) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.nome,
            onValueChange = onNomeChange,
            label = { Text("Nome do projeto") },
            isError = state.nomeError != null,
            supportingText = {
                if (state.nomeError != null) Text(state.nomeError)
                else Text("${state.nome.length}/${ProjetoFormViewModel.NOME_MAX}")
            },
            singleLine = true,
            enabled = !state.submetendo,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.descricao,
            onValueChange = onDescricaoChange,
            label = { Text("Descrição (opcional)") },
            isError = state.descricaoError != null,
            supportingText = {
                if (state.descricaoError != null) Text(state.descricaoError)
                else Text("${state.descricao.length}/${ProjetoFormViewModel.DESCRICAO_MAX}")
            },
            enabled = !state.submetendo,
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        CampoPrazo(
            prazo = state.prazo,
            onPrazoChange = onPrazoChange,
            prazoError = state.prazoError,
            enabled = !state.submetendo,
        )

        state.mensagemErroGeral?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onCancelar,
                enabled = !state.submetendo,
                modifier = Modifier.weight(1f),
            ) { Text("Cancelar") }

            Button(
                onClick = onSalvar,
                enabled = state.podeSubmeter,
                modifier = Modifier.weight(1f),
            ) {
                if (state.submetendo) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(if (state.ehEdicao) "Atualizar" else "Salvar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoPrazo(
    prazo: LocalDate?,
    onPrazoChange: (LocalDate?) -> Unit,
    prazoError: String?,
    enabled: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = prazo?.format(BRASIL_DATA_FORMAT).orEmpty(),
        onValueChange = { /* read-only — escolha via DatePicker */ },
        label = { Text("Prazo (opcional)") },
        isError = prazoError != null,
        supportingText = prazoError?.let { { Text(it) } },
        readOnly = true,
        enabled = enabled,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }, enabled = enabled) {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Selecionar prazo")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )

    if (prazo != null) {
        TextButton(
            onClick = { onPrazoChange(null) },
            enabled = enabled,
            modifier = Modifier.padding(top = 4.dp),
        ) { Text("Limpar prazo") }
    }

    if (showDialog) {
        val initialMillis = prazo
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
            ?: Instant.now().toEpochMilli()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val novaData = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onPrazoChange(novaData)
                    }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun Carregando(innerPadding: androidx.compose.foundation.layout.PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

private val BRASIL_DATA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale("pt", "BR"))

@Preview(showBackground = true)
@Composable
private fun CriacaoScreenPreview() {
    BrainOutAppTheme {
        CriacaoScreen()
    }
}