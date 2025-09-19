package com.alican.satellites.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alican.satellites.ui.screens.detail.SatelliteDetailScreen
import com.alican.satellites.ui.screens.list.SatelliteListScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SatelliteList,
        modifier = modifier
    ) {
        composable<ScreenRoutes.SatelliteList> {
            SatelliteListScreen(
                onNavigateToDetail = { id ->
                    val route = ScreenRoutes.SatelliteDetail(id = id)
                    navController.navigate(route = route)
                }
            )
        }

        composable<ScreenRoutes.SatelliteDetail> {
            SatelliteDetailScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}