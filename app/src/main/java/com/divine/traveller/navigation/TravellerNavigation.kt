package com.divine.traveller.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.divine.traveller.ui.home.HomeScreen
import com.divine.traveller.ui.trips.NewTripScreen

@Composable
fun TravellerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToNewTrip = {
                    navController.navigate(Routes.NEW_TRIP)
                }
            )
        }

        composable(Routes.NEW_TRIP) {
            NewTripScreen(
                onTripCreated = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}