package com.joaopedrogms.brainoutapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Tema Material 3 do BrainOutApp.
 *
 * Aplica `ColorScheme` (light ou dark — ver `Color.kt`) e `Typography` (ver `Type.kt`).
 * Os valores seguem `docs/design-system.md` — qualquer desvio aqui deve refletir (ou ser
 * refletido) naquele documento.
 *
 * @param darkTheme   quando `null`, usa `isSystemInDarkTheme()`; quando definido,
 *                    força o modo correspondente. Útil para preview e testes.
 * @param content     conteúdo Composable aninhado que consumirá `MaterialTheme.*`.
 */
@Composable
fun BrainOutAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BrainOutTypography,
        content = content,
    )
}