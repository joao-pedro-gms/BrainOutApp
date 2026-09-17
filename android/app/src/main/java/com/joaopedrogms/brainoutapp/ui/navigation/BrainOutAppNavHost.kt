package com.joaopedrogms.brainoutapp.ui.navigation

import androidx.compose.runtime.Composable
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
import com.joaopedrogms.brainoutapp.ui.screens.projetos.ProjetosScreen
import com.joaopedrogms.brainoutapp.ui.screens.tarefas.TarefasScreen

/**
 * NavHost do app. Define o grafo de navegação entre as 6 telas do protótipo.
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
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN,
    ) {
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
