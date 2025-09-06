package com.divine.traveller.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.divine.traveller.ui.home.HomeScreen
import com.divine.traveller.ui.trips.NewTripScreen
import com.divine.traveller.ui.trips.TripItineraryScreen

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
                },
                onNavigateToTripDetails = { tripId ->
                    navController.navigate("trip_details/$tripId")
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

        composable(
            route = "trip_details/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId")
            if (tripId != null) {
                TripItineraryScreen(
                    tripId = tripId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}