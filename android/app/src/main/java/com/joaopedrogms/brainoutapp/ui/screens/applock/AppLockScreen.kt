package com.joaopedrogms.brainoutapp.ui.screens.applock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme

/**
 * Tela de bloqueio (AppLock) — exibida no cold start quando o usuário
 * configurou uma senha local. Ver [com.joaopedrogms.brainoutapp.viewmodel.RootStartState.NeedsUnlock].
 *
 * Comportamento:
 *  - campo de senha (mascarado);
 *  - "Desbloquear" valida via [AppLockViewModel];
 *  - "Esqueci a senha" abre um `AlertDialog` explicando que **não há
 *    recovery** — se confirmar, o AppLock é removido (perfil preservado);
 *  - 3 falhas → cooldown de 30s (gerenciado no ViewModel).
 *
 * Origem: ADR-0006 — AppLock opcional.
 *
 * Não depende do NavHost (é uma tela standalone renderizada quando
 * [com.joaopedrogms.brainoutapp.viewmodel.RootStartState.NeedsUnlock] é
 *  o estado atual). Após desbloqueio, o `RootViewModel.refresh()`
 *  promovido pela navegação libera o NavHost.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockScreen(
    onUnlocked: () -> Unit = {},
    viewModel: AppLockViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unlocked by viewModel.unlocked.collectAsStateWithLifecycle()

    // Quando o VM confirma o unlock (sucesso OU "esqueci a senha"),
    // a navegação libera o NavHost.
    LaunchedEffect(unlocked) {
        if (unlocked) onUnlocked()
    }

    var senha by rememberSaveable { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bloqueio do app") }) },
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
                text = "App bloqueado",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Digite sua senha para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            val isLoading = uiState is AppLockViewModel.AppLockUiState.Loading
            val isCooldown = uiState is AppLockViewModel.AppLockUiState.Cooldown
            val errorMessage = (uiState as? AppLockViewModel.AppLockUiState.Error)?.message
            val cooldownSeconds =
                (uiState as? AppLockViewModel.AppLockUiState.Cooldown)?.remainingSeconds

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                singleLine = true,
                enabled = !isCooldown,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.unlock(senha)
                    },
                ),
                isError = errorMessage != null,
                supportingText = {
                    when {
                        cooldownSeconds != null -> Text(
                            "Muitas tentativas. Aguarde $cooldownSeconds s…",
                        )
                        errorMessage != null -> Text(errorMessage)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { viewModel.unlock(senha) },
                enabled = !isLoading && !isCooldown && senha.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
            ) {
                Text(if (isLoading) "Validando…" else "Desbloquear")
            }

            OutlinedButton(
                onClick = { showForgotDialog = true },
                enabled = !isLoading && !isCooldown,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text("Esqueci a senha")
            }
        }
    }

    if (showForgotDialog) {
        ForgotSenhaDialog(
            onConfirm = {
                showForgotDialog = false
                viewModel.forgotSenha()
            },
            onDismiss = { showForgotDialog = false },
        )
    }
}

/**
 * Dialog de confirmação — "Esqueci a senha". Explica explicitamente
 * que **não há recovery** e que isso só remove o AppLock (não apaga
 * dados). Origem: ADR-0006.
 */
@Composable
private fun ForgotSenhaDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Esqueci a senha") },
        text = {
            Text(
                "O BrainOutApp não armazena sua senha — não é possível recuperá-la. " +
                    "Se continuar, o bloqueio do app será removido. Seus dados (perfil, " +
                    "projetos, tarefas) permanecerão intactos.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Remover bloqueio") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AppLockScreenPreview() {
    BrainOutAppTheme {
        // Preview sem VM (estado Idle implícito). Não dispara nada.
        AppLockScreen(onUnlocked = {})
    }
}
