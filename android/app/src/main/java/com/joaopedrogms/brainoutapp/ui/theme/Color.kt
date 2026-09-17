package com.joaopedrogms.brainoutapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Tokens semânticos de cor do BrainOutApp.
 *
 * Os valores hexadecimais aqui devem estar sincronizados com `docs/design-system.md`
 * (seções 2.1 e 2.2). Toda referência visual no app deve usar um destes tokens —
 * nunca `Color(0x...)` literal em composables.
 *
 * Convenção de nomenclatura (papel semântico, não cor):
 *  - `surface*`     → papéis de fundo (fundo da tela, cards, containers).
 *  - `onSurface*`   → conteúdo sobre `surface*` (texto, ícone). Garante contraste WCAG AA.
 *  - `accent*`      → destaque funcional (primário/CTA, secundário).
 *  - `onAccent`     → conteúdo sobre qualquer `accent*`.
 *  - `state*`       → feedback semântico (success, warning, error, info).
 *  - `outline*`     → bordas e divisores (componente ≥ 3 : 1; decorativo sem requisito).
 */

// =====================================================================================
// LIGHT SCHEME — tokens semânticos (modo claro)
// =====================================================================================

// Superfícies
val SurfacePrimaryLight   = Color(0xFFFFFFFF)  // background / surface / scaffold
val SurfaceSecondaryLight = Color(0xFFF1F4F1)  // cards, diálogos, sheets
val SurfaceTertiaryLight  = Color(0xFFE3E8E4)  // containers sutis, chips neutros

// Conteúdo sobre superfícies (textos e ícones)
val OnSurfacePrimaryLight   = Color(0xFF191C1A)  // texto principal (17.2 : 1 — AAA)
val OnSurfaceSecondaryLight = Color(0xFF414942)  // texto secundário (9.3 : 1 — AAA)
val OnSurfaceTertiaryLight  = Color(0xFF5C6560)  // texto terciário (6.0 : 1 — AA)

// Acentos (verde — alinhado aos wireframes do protótipo)
val AccentPrimaryLight    = Color(0xFF1F6F4A)  // botões primários, links, foco (6.1 : 1)
val AccentSecondaryLight  = Color(0xFF4D6358)  // botões secundários, toggles (6.5 : 1)
val OnAccentLight         = Color(0xFFFFFFFF)  // conteúdo sobre qualquer accent

// Estados semânticos (feedback)
val StateSuccessLight = Color(0xFF1B7F47)  // 5.0 : 1
val StateWarningLight = Color(0xFFB45309)  // 5.0 : 1
val StateErrorLight   = Color(0xFFB3261E)  // 6.5 : 1
val StateInfoLight    = Color(0xFF1F6FB5)  // 5.3 : 1

// Bordas e divisores
val OutlineLight        = Color(0xFF717971)  // bordas de input (4.5 : 1 — componente)
val OutlineVariantLight = Color(0xFFC1C9C2)  // decorativo (sem requisito de contraste)

// =====================================================================================
// DARK SCHEME — tokens semânticos (modo escuro)
// =====================================================================================

// Superfícies (fundo quase-preto com matiz verde para coerência com o brand)
val SurfacePrimaryDark   = Color(0xFF101411)
val SurfaceSecondaryDark = Color(0xFF1A1F1B)
val SurfaceTertiaryDark  = Color(0xFF252B27)

// Conteúdo sobre superfícies
val OnSurfacePrimaryDark   = Color(0xFFE1E3DE)  // 14.4 : 1 — AAA
val OnSurfaceSecondaryDark = Color(0xFFC1C9C2)  // 11.0 : 1 — AAA
val OnSurfaceTertiaryDark  = Color(0xFFA0A89F)  // 7.6 : 1 — AAA

// Acentos (verde claro para legibilidade em fundo escuro)
val AccentPrimaryDark    = Color(0xFF9CD4B9)  // 11.1 : 1 sobre surfacePrimary
val AccentSecondaryDark  = Color(0xFFB3CCBE)  // 10.8 : 1
val OnAccentDark         = Color(0xFF003824)  // conteúdo sobre accent (7.9 : 1)

// Estados semânticos
val StateSuccessDark = Color(0xFF6FE0A0)  // 11.4 : 1 — AAA
val StateWarningDark = Color(0xFFFBBF24)  // 10.9 : 1 — AAA
val StateErrorDark   = Color(0xFFF2B8B5)  // 10.9 : 1 — AAA
val StateInfoDark    = Color(0xFF9DC2F0)  // 9.8 : 1 — AAA

// Bordas e divisores
val OutlineDark        = Color(0xFF8B938C)  // 5.9 : 1 — componente
val OutlineVariantDark = Color(0xFF414942)  // decorativo

// =====================================================================================
// Material 3 ColorScheme — mapeia tokens semânticos nos slots do Material 3
// (ver `docs/design-system.md` seção 2.3 para a tabela completa)
// =====================================================================================

internal val LightColors = lightColorScheme(
    primary               = AccentPrimaryLight,
    onPrimary             = OnAccentLight,
    primaryContainer      = SurfaceTertiaryLight,
    onPrimaryContainer    = AccentPrimaryLight,
    secondary             = AccentSecondaryLight,
    onSecondary           = OnAccentLight,
    secondaryContainer    = SurfaceTertiaryLight,
    onSecondaryContainer  = AccentSecondaryLight,
    tertiary              = StateInfoLight,
    onTertiary            = OnAccentLight,
    tertiaryContainer     = SurfaceTertiaryLight,
    onTertiaryContainer   = StateInfoLight,
    background            = SurfacePrimaryLight,
    onBackground          = OnSurfacePrimaryLight,
    surface               = SurfacePrimaryLight,
    onSurface             = OnSurfacePrimaryLight,
    surfaceVariant        = SurfaceSecondaryLight,
    onSurfaceVariant      = OnSurfaceSecondaryLight,
    surfaceTint           = AccentPrimaryLight,
    inverseSurface        = SurfacePrimaryDark,
    inverseOnSurface      = OnSurfacePrimaryDark,
    inversePrimary        = AccentPrimaryDark,
    error                 = StateErrorLight,
    onError               = OnAccentLight,
    errorContainer        = SurfaceTertiaryLight,
    onErrorContainer      = StateErrorLight,
    outline               = OutlineLight,
    outlineVariant        = OutlineVariantLight,
    scrim                 = Color(0xFF000000),
)

internal val DarkColors = darkColorScheme(
    primary               = AccentPrimaryDark,
    onPrimary             = OnAccentDark,
    primaryContainer      = SurfaceTertiaryDark,
    onPrimaryContainer    = AccentPrimaryDark,
    secondary             = AccentSecondaryDark,
    onSecondary           = OnAccentDark,
    secondaryContainer    = SurfaceTertiaryDark,
    onSecondaryContainer  = AccentSecondaryDark,
    tertiary              = StateInfoDark,
    onTertiary            = OnAccentDark,
    tertiaryContainer     = SurfaceTertiaryDark,
    onTertiaryContainer   = StateInfoDark,
    background            = SurfacePrimaryDark,
    onBackground          = OnSurfacePrimaryDark,
    surface               = SurfacePrimaryDark,
    onSurface             = OnSurfacePrimaryDark,
    surfaceVariant        = SurfaceSecondaryDark,
    onSurfaceVariant      = OnSurfaceSecondaryDark,
    surfaceTint           = AccentPrimaryDark,
    inverseSurface        = SurfacePrimaryLight,
    inverseOnSurface      = OnSurfacePrimaryLight,
    inversePrimary        = AccentPrimaryLight,
    error                 = StateErrorDark,
    onError               = OnAccentDark,
    errorContainer        = SurfaceTertiaryDark,
    onErrorContainer      = StateErrorDark,
    outline               = OutlineDark,
    outlineVariant        = OutlineVariantDark,
    scrim                 = Color(0xFF000000),
)