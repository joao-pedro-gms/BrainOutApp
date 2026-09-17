package com.joaopedrogms.brainoutapp.ui.screens.dashboard

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
 * Tela 4 — Dashboard (wireframe 06-dashboard.svg).
 * Stub sem lógica. KPIs + gráficos serão implementados quando a camada de
 * dados (Room) estiver pronta (issue #13 escolhe a biblioteca de gráficos).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVoltar: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
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
                text = "Tela Dashboard",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Stub — KPIs e gráficos virão na issue #13",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    BrainOutAppTheme {
        DashboardScreen()
    }
}
