package com.joaopedrogms.brainoutapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Entry point da aplicação. A anotação `@HiltAndroidApp` dispara a geração
 * do `Hilt_BrainOutApp` (KSP) que inicializa o container de DI antes de
 * qualquer Activity tocar em dependências.
 *
 * Ciclos posteriores (R6, R7) adicionarão:
 *  - WorkManager custom Configuration (sync offline)
 *  - Crash reporting / Timber
 *  - Estratégia de token storage (EncryptedSharedPreferences)
 */
@HiltAndroidApp
class BrainOutApp : Application()
