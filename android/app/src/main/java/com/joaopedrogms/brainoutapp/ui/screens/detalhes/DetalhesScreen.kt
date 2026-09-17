package com.joaopedrogms.brainoutapp.ui.screens.detalhes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
 * Tela 6 — Detalhes do projeto (wireframe 03-projeto-detalhe.svg).
 * Stub sem lógica. Recebe `projetoId` via argumento de rota
 * (`Destinations.DETALHES_PROJETO_ARG`) — ciclos 2+ resolvem o id em
 * `viewmodel/` e carregam do `repository/`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    projetoId: String,
    onVoltar: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalhes do projeto") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Tela Detalhes",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "projetoId recebido: $projetoId",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Stub — tarefas e histórico virão na issue de detalhes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetalhesScreenPreview() {
    BrainOutAppTheme {
        DetalhesScreen(projetoId = "preview-id")
    }
}
