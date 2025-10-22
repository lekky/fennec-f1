package com.f1calendar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.f1calendar.data.model.Race
import com.f1calendar.ui.screen.RaceDetailScreen
import com.f1calendar.ui.viewmodel.RaceDetailViewModel

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object RaceDetail : Screen("race_detail/{round}/{raceName}")

    fun createRoute(vararg args: Any): String {
        var route = this.route
        args.forEach { arg ->
            route = route.replaceFirst(Regex("\\{[^}]+\\}"), arg.toString())
        }
        return route
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    raceDetailViewModel: RaceDetailViewModel,
    currentRace: Race?,
    mainScreen: @Composable () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            mainScreen()
        }

        composable(Screen.RaceDetail.route) {
            currentRace?.let { race ->
                RaceDetailScreen(
                    race = race,
                    viewModel = raceDetailViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
