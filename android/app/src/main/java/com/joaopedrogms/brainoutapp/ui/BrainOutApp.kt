package com.joaopedrogms.brainoutapp.ui

import androidx.compose.runtime.Composable
import com.joaopedrogms.brainoutapp.ui.navigation.BrainOutAppNavHost

/**
 * Composable raiz da camada de UI. Hospeda o NavHost (Compose Navigation)
 * e serve de ponto único de entrada para todas as telas do app.
 *
 * Não contém lógica de negócio — apenas delega ao NavHost. R12.
 */
@Composable
fun BrainOutApp() {
    BrainOutAppNavHost()
}
