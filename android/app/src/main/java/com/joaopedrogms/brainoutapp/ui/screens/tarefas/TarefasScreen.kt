package com.joaopedrogms.brainoutapp.ui.screens.tarefas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme

/**
 * Tela 3 — Lista global de tarefas com filtros (wireframe 04-tarefas.svg).
 * Stub sem lógica. Implementação real virá na issue de gestão de tarefas
 * (ciclo 2 — R9) com ViewModel + UseCase + Repository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefasScreen(
    onNovaTarefa: () -> Unit = {},
    onVoltar: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarefas") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Tela Tarefas",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Stub — filtros e lista virão na issue de tarefas",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onNovaTarefa,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text("Nova tarefa")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarefasScreenPreview() {
    BrainOutAppTheme {
        TarefasScreen()
    }
}
