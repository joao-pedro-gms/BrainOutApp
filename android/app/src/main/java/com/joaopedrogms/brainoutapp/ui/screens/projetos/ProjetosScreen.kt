package com.joaopedrogms.brainoutapp.ui.screens.projetos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
 * Tela 2 — Lista de projetos (wireframe 02-projetos.svg).
 * Stub sem lógica. Botões disparam as 4 navegações do app:
 *  - "Abrir projeto"        → Detalhes
 *  - "Novo projeto"         → Criação
 *  - "Ver tarefas"          → Tarefas
 *  - "Abrir dashboard"      → Dashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetosScreen(
    onAbrirProjeto: (String) -> Unit = {},
    onNovoProjeto: () -> Unit = {},
    onAbrirTarefas: () -> Unit = {},
    onAbrirDashboard: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Projetos") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Tela Projetos",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Button(
                onClick = { onAbrirProjeto("projeto-demo-1") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Abrir projeto (stub)")
            }

            Button(
                onClick = onNovoProjeto,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Novo projeto")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onAbrirTarefas,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Tarefas")
                }
                OutlinedButton(
                    onClick = onAbrirDashboard,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Dashboard")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProjetosScreenPreview() {
    BrainOutAppTheme {
        ProjetosScreen()
    }
}
