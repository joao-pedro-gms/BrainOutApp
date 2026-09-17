package com.joaopedrogms.brainoutapp.ui.screens.criacao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
 * Tela 5 — Formulário de criação (wireframe 05-tarefa-form.svg).
 * Stub sem lógica. Campos não persistem nem validam — RN03 (prazo tarefa vs
 * projeto) será validada em `domain/usecase/` na issue correspondente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriacaoScreen(
    onVoltar: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Criar projeto / tarefa") })
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
                text = "Tela Criação",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Stub — formulário será ligado ao ViewModel na issue de criação.",
                style = MaterialTheme.typography.bodyMedium,
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Título (stub)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Descrição (stub)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onVoltar,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Salvar (stub)")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CriacaoScreenPreview() {
    BrainOutAppTheme {
        CriacaoScreen()
    }
}
