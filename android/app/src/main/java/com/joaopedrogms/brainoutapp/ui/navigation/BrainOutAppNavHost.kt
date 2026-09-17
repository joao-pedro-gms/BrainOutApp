package com.joaopedrogms.brainoutapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joaopedrogms.brainoutapp.ui.screens.criacao.CriacaoScreen
import com.joaopedrogms.brainoutapp.ui.screens.dashboard.DashboardScreen
import com.joaopedrogms.brainoutapp.ui.screens.detalhes.DetalhesScreen
import com.joaopedrogms.brainoutapp.ui.screens.login.LoginScreen
import com.joaopedrogms.brainoutapp.ui.screens.onboarding.OnboardingScreen
import com.joaopedrogms.brainoutapp.ui.screens.projetos.ProjetosScreen
import com.joaopedrogms.brainoutapp.ui.screens.tarefas.TarefasScreen
import com.joaopedrogms.brainoutapp.viewmodel.RootStartState
import com.joaopedrogms.brainoutapp.viewmodel.RootViewModel

/**
 * NavHost do app.
 *
 * Define o grafo de navegação entre as telas e decide a start
 * destination com base no perfil local (ADR-0006):
 *
 *  - perfil indefinido → `onboarding` (escolha de Gerente/Colaborador).
 *  - perfil definido    → `projetos` (home; `login` permanece no grafo
 *                          por enquanto como stub — será reavaliada em
 *                          ciclo futuro, já que ADR-0006 eliminou auth
 *                          online).
 *
 * A decisão da start destination fica em [RootViewModel] (não dá para
 * fazer `suspend` dentro do `NavHost(startDestination = …)`).
 *
 * Por enquanto as transições são default (slide horizontal). Ciclos
 * posteriores adicionarão deep links (issue #15 — notificações R8).
 *
 * Sem ViewModel aqui: navegação é responsabilidade da camada de UI e é
 * puramente declarativa. R12.
 */
@Composable
fun BrainOutAppNavHost(
    navController: NavHostController = rememberNavController(),
    rootViewModel: RootViewModel = hiltViewModel(),
) {
    val startState by rootViewModel.state.collectAsStateWithLifecycle()

    when (startState) {
        RootStartState.Loading -> RootLoadingScreen()
        RootStartState.NeedsOnboarding, RootStartState.Authenticated -> {
            val startDestination = when (startState) {
                RootStartState.NeedsOnboarding -> Destinations.ONBOARDING
                RootStartState.Authenticated -> Destinations.PROJETOS
                else -> Destinations.ONBOARDING // fallback (nunca alcança)
            }
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable(Destinations.ONBOARDING) {
                    OnboardingScreen(
                        onProfileChosen = { _ ->
                            navController.navigate(Destinations.PROJETOS) {
                                // Substitui a rota de onboarding para não voltar via back.
                                popUpTo(Destinations.ONBOARDING) { inclusive = true }
                            }
                            // Reavalia o estado raiz para que uma nova escolha
                            // (ex.: Configurações → outro perfil) reflita no app.
                            rootViewModel.refresh()
                        },
                    )
                }

                composable(Destinations.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Destinations.PROJETOS) {
                                // Substitui a rota de login para não voltar via back.
                                popUpTo(Destinations.LOGIN) { inclusive = true }
                            }
                        },
                    )
                }

                composable(Destinations.PROJETOS) {
                    ProjetosScreen(
                        onAbrirProjeto = { id ->
                            navController.navigate(Destinations.detalhesProjeto(id))
                        },
                        onNovoProjeto = {
                            navController.navigate(Destinations.CRIACAO)
                        },
                        onAbrirTarefas = {
                            navController.navigate(Destinations.TAREFAS)
                        },
                        onAbrirDashboard = {
                            navController.navigate(Destinations.DASHBOARD)
                        },
                    )
                }

                composable(
                    route = Destinations.DETALHES_PROJETO_ROUTE,
                    arguments = listOf(
                        navArgument(Destinations.DETALHES_PROJETO_ARG) { type = NavType.StringType },
                    ),
                ) { backStackEntry ->
                    val projetoId = backStackEntry.arguments
                        ?.getString(Destinations.DETALHES_PROJETO_ARG)
                        .orEmpty()
                    DetalhesScreen(
                        projetoId = projetoId,
                        onVoltar = { navController.popBackStack() },
                    )
                }

                composable(Destinations.TAREFAS) {
                    TarefasScreen(
                        onNovaTarefa = {
                            navController.navigate(Destinations.CRIACAO)
                        },
                        onVoltar = { navController.popBackStack() },
                    )
                }

                composable(Destinations.CRIACAO) {
                    CriacaoScreen(
                        onVoltar = { navController.popBackStack() },
                    )
                }

                composable(Destinations.DASHBOARD) {
                    DashboardScreen(
                        onVoltar = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

/**
 * Tela de loading exibida enquanto o [RootViewModel] lê o DataStore.
 *
 * Mantida simples (Scaffold + CircularProgressIndicator) — o cold
 * start deve terminar em poucos ms na primeira execução (arquivo de
 * preferências vazio) e em ~0 ms nas seguintes (cache em memória do
 * DataStore). Ciclos futuros podem trocar por um splash screen oficial
 * (issue #15 — R8).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootLoadingScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("BrainOutApp") }) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
