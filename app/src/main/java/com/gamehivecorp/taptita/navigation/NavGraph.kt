package com.gamehivecorp.taptita.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.sound.SoundManager
import com.gamehivecorp.taptita.ui.screens.LeaderboardScreen
import com.gamehivecorp.taptita.ui.screens.MenuScreen
import com.gamehivecorp.taptita.ui.screens.PrivacyScreen
import com.gamehivecorp.taptita.ui.screens.SlotGameScreen
import com.gamehivecorp.taptita.viewmodel.MenuViewModel

object Routes {
    const val MENU = "menu"
    const val GAME = "game/{themeId}"
    const val LEADERBOARD = "leaderboard"
    const val PRIVACY = "privacy"
    const val LOADING = "loading"
    const val CONNECT = "connect"
    fun gameRoute(themeId: Int) = "game/$themeId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    soundManager: SoundManager,
    menuViewModel: MenuViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = Routes.MENU) {
        composable(Routes.MENU) {
            MenuScreen(
                viewModel = menuViewModel,
                onPlayGame = { theme ->
                    navController.navigate(Routes.gameRoute(theme.ordinal))
                },
                onLeaderboard = {
                    navController.navigate(Routes.LEADERBOARD)
                },
                onPrivacy = {
                    navController.navigate(Routes.PRIVACY)
                }
            )
        }

        composable(Routes.GAME) { backStackEntry ->
            val themeId = backStackEntry.arguments?.getString("themeId")?.toIntOrNull() ?: 0
            val theme = GameTheme.entries.getOrElse(themeId) { GameTheme.AZTEC }
            SlotGameScreen(
                theme = theme,
                soundManager = soundManager,
                onBack = {
                    menuViewModel.refreshState()
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                viewModel = menuViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PRIVACY) {
            PrivacyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
