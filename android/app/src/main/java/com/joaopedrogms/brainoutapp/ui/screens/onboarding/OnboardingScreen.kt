package com.joaopedrogms.brainoutapp.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.data.preferences.Perfil
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme

/**
 * Tela de Onboarding — escolha de perfil local (Gerente / Colaborador).
 *
 * Origem: ADR-0006. Exibida uma única vez, no primeiro cold start do
 * app (gate feito no NavHost via [com.joaopedrogms.brainoutapp.ui.navigation.RootViewModel]).
 *
 * Visual: dois botões grandes (filled / outlined) seguindo os tokens
 * semânticos do design system — `MaterialTheme.colorScheme.primary`
 * para o CTA "Gerente" e `secondary` para "Colaborador" (ambos
 * com `heightIn(min = 56.dp)` para área de toque ≥ 48dp WCAG).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onProfileChosen: (Perfil) -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bem-vindo ao BrainOutApp") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quem é você?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Escolha seu perfil. Você poderá mudar depois em Configurações.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CTA primário — Gerente (cria projetos, atribui tarefas, vê dashboard).
            Button(
                onClick = { viewModel.setPerfil(Perfil.GERENTE, { onProfileChosen(Perfil.GERENTE) }) },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
            ) {
                Text(
                    text = "Sou Gerente",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            // CTA secundário — Colaborador (vê só o que é dele).
            OutlinedButton(
                onClick = { viewModel.setPerfil(Perfil.COLABORADOR, { onProfileChosen(Perfil.COLABORADOR) }) },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
            ) {
                Text(
                    text = "Sou Colaborador",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            // Mensagem de erro transitória (não-bloqueante).
            if (error != null) {
                Text(
                    text = error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    BrainOutAppTheme {
        // Preview usa o VM real (Hilt provê em runtime); o estado saving=false,
        // error=null mantém os dois botões habilitados para o preview.
        OnboardingScreen()
    }
}
