package com.joaopedrogms.brainoutapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.joaopedrogms.brainoutapp.ui.BrainOutApp
import com.joaopedrogms.brainoutapp.ui.theme.BrainOutAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity única. Hospeda o NavHost (ver `ui/navigation/BrainOutAppNavHost.kt`)
 * e o tema Material 3 (`ui/theme/Theme.kt`).
 *
 * Nenhuma regra de negócio vive aqui — R12 (arquitetura.md §Camadas).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrainOutAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    BrainOutApp()
                }
            }
        }
    }
}
