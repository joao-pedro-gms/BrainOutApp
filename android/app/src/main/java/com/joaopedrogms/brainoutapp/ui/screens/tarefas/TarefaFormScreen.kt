package com.joaopedrogms.brainoutapp.ui.screens.tarefas

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.domain.model.PrioridadeTarefa
import com.joaopedrogms.brainoutapp.domain.model.Projeto
import com.joaopedrogms.brainoutapp.domain.model.StatusTarefa
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import com.joaopedrogms.brainoutapp.viewmodel.TarefaFormViewModel
import com.joaopedrogms.brainoutapp.viewmodel.UiState
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Tela 5 (variante) — Formulário de **tarefa** (criar e editar).
 *
 * Esta tela é uma **variante específica** do [com.joaopedrogms.brainoutapp
 * .ui.screens.criacao.CriacaoScreen] que existe para projetos. Mantemos
 * as duas separadas (opção A do briefing da lane #10) porque o form de
 * tarefa tem um seletor de projeto pai (FK) que o form de projeto não
 * tem, e os campos divergem em prioridade/status/responsável.
 *
 * Roteamento (`criacao/tarefa?projetoId=&tarefaId=`):
 *  - sem `tarefaId` → modo criar; `projetoId` pode vir pré-selecionado.
 *  - com `tarefaId` → modo editar; o `TarefaFormViewModel` carrega do DAO.
 *
 * Comportamento (espelha `CriacaoScreen`):
 *  - **Carregando**: spinner.
 *  - **Pronto**: form com projeto (dropdown) / título / descrição / prazo
 *    (DatePicker) / prioridade (chips) / status (chips) / responsável.
 *    Cada campo dispara validação on-the-fly (mensagens abaixo do input).
 *  - **Submetendo**: botões desabilitados, "Salvando…".
 *  - **Sem projetos**: empty state pedindo para criar um projeto antes.
 *  - **Erro de validação**: campo com `isError = true` + mensagem.
 *  - **Erro geral**: texto abaixo do form.
 *  - **Concluído** (one-shot): volta para a tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefaFormScreen(
    onVoltar: () -> Unit = {},
    viewModel: TarefaFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val projetosState by viewModel.projetos.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { evt ->
            when (evt) {
                TarefaFormViewModel.Event.Concluido -> onVoltar()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.ehEdicao) "Editar tarefa" else "Nova tarefa") },
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
                projetosState = projetosState,
                onProjetoChange = viewModel::onProjetoChange,
                onTituloChange = viewModel::onTituloChange,
                onDescricaoChange = viewModel::onDescricaoChange,
                onPrazoChange = viewModel::onPrazoChange,
                onPrioridadeChange = viewModel::onPrioridadeChange,
                onStatusChange = viewModel::onStatusChange,
                onResponsavelChange = viewModel::onResponsavelChange,
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
    state: TarefaFormViewModel.FormState,
    projetosState: UiState<List<Projeto>>,
    onProjetoChange: (String?) -> Unit,
    onTituloChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onPrazoChange: (LocalDate?) -> Unit,
    onPrioridadeChange: (PrioridadeTarefa) -> Unit,
    onStatusChange: (StatusTarefa) -> Unit,
    onResponsavelChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
) {
    // Sem projetos cadastrados: orientamos o usuário antes de mostrar o form.
    if (projetosState is UiState.Success && projetosState.data.isEmpty()) {
        EmptySemProjetos(innerPadding, onCancelar)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SeletorProjeto(
            projetos = (projetosState as? UiState.Success)?.data.orEmpty(),
            projetoIdSelecionado = state.projetoId,
            onProjetoChange = onProjetoChange,
            erro = state.projetoError,
            enabled = !state.submetendo && !state.ehEdicao,
        )

        OutlinedTextField(
            value = state.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título da tarefa") },
            isError = state.tituloError != null,
            supportingText = {
                if (state.tituloError != null) Text(state.tituloError)
                else Text("${state.titulo.length}/${TarefaFormViewModel.TITULO_MAX}")
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
                else Text("${state.descricao.length}/${TarefaFormViewModel.DESCRICAO_MAX}")
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

        SeletorPrioridade(
            atual = state.prioridade,
            onChange = onPrioridadeChange,
            enabled = !state.submetendo,
        )

        SeletorStatus(
            atual = state.status,
            onChange = onStatusChange,
            enabled = !state.submetendo,
        )

        OutlinedTextField(
            value = state.responsavel,
            onValueChange = onResponsavelChange,
            label = { Text("Responsável (opcional)") },
            singleLine = true,
            enabled = !state.submetendo,
            modifier = Modifier.fillMaxWidth(),
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
private fun SeletorProjeto(
    projetos: List<Projeto>,
    projetoIdSelecionado: String?,
    onProjetoChange: (String?) -> Unit,
    erro: String?,
    enabled: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    val selecionado = projetos.firstOrNull { it.id == projetoIdSelecionado }

    Column(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.material3.ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled && !expanded) expanded = true },
        ) {
            OutlinedTextField(
                value = selecionado?.nome ?: "Selecione um projeto",
                onValueChange = { /* read-only via dropdown */ },
                readOnly = true,
                label = { Text("Projeto") },
                trailingIcon = {
                    IconButton(onClick = { if (enabled) expanded = !expanded }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Abrir lista")
                    }
                },
                isError = erro != null,
                supportingText = erro?.let { { Text(it) } },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                if (projetos.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Nenhum projeto disponível") },
                        onClick = { expanded = false },
                        enabled = false,
                    )
                } else {
                    projetos.forEach { projeto ->
                        DropdownMenuItem(
                            text = { Text(projeto.nome) },
                            onClick = {
                                onProjetoChange(projeto.id)
                                expanded = false
                            },
                        )
                    }
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
private fun SeletorPrioridade(
    atual: PrioridadeTarefa,
    onChange: (PrioridadeTarefa) -> Unit,
    enabled: Boolean,
) {
    Column {
        Text(
            text = "Prioridade",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PrioridadeTarefa.entries.forEach { p ->
                FilterChip(
                    selected = p == atual,
                    onClick = { if (enabled) onChange(p) },
                    label = { Text(labelPrioridade(p)) },
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
private fun SeletorStatus(
    atual: StatusTarefa,
    onChange: (StatusTarefa) -> Unit,
    enabled: Boolean,
) {
    Column {
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusTarefa.entries.forEach { s ->
                FilterChip(
                    selected = s == atual,
                    onClick = { if (enabled) onChange(s) },
                    label = { Text(labelStatusTarefa(s)) },
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
private fun EmptySemProjetos(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onCancelar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Crie um projeto primeiro",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Para criar tarefas é preciso ter ao menos um projeto cadastrado. " +
                "Volte à lista de projetos e crie um antes de continuar.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.padding(top = 16.dp),
        ) { Text("Voltar") }
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

private fun labelStatusTarefa(status: StatusTarefa): String = when (status) {
    StatusTarefa.ABERTA -> "Aberta"
    StatusTarefa.EM_ANDAMENTO -> "Em andamento"
    StatusTarefa.CONCLUIDA -> "Concluída"
    StatusTarefa.CANCELADA -> "Cancelada"
}

private fun labelPrioridade(prioridade: PrioridadeTarefa): String = when (prioridade) {
    PrioridadeTarefa.BAIXA -> "Baixa"
    PrioridadeTarefa.MEDIA -> "Média"
    PrioridadeTarefa.ALTA -> "Alta"
    PrioridadeTarefa.URGENTE -> "Urgente"
}

private val BRASIL_DATA_FORMAT =
    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale("pt", "BR"))

// =====================================================================================
// Previews
// =====================================================================================

@Preview(showBackground = true)
@Composable
private fun TarefaFormScreenPreview() {
    BrainOutAppTheme {
        TarefaFormScreen()
    }
}
