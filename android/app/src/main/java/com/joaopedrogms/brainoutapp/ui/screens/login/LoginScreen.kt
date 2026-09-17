package com.joaopedrogms.brainoutapp.ui.screens.login

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
 * Tela 1 — Login (wireframe 01-login.svg).
 * Stub sem lógica: botão avança para a lista de projetos (issue #7 implementará
 * a autenticação real com 2 perfis — Gerente / Colaborador).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BrainOutApp — Login") })
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
                text = "Tela Login",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Stub — autenticação real chega na issue #7",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text("Entrar (stub)")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BrainOutAppTheme {
        LoginScreen()
    }
}
