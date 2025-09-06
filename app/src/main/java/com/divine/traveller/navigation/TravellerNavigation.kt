package com.divine.traveller.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.divine.traveller.ui.budget.BudgetScreen
import com.divine.traveller.ui.flight.FlightScreen
import com.divine.traveller.ui.home.HomeScreen
import com.divine.traveller.ui.hotel.HotelScreen
import com.divine.traveller.ui.trip.NewTripScreen
import com.divine.traveller.ui.trip.TripItineraryScreen

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

        // Reused helper for screens that accept a long tripId and use the same navigate options
        detailScreen(
            navController = navController,
            routeBase = Routes.TRIP_DETAILS
        ) { tripId, onNavigate, onNavigateBack ->
            TripItineraryScreen(
                tripId = tripId,
                onNavigate = onNavigate,
                onNavigateBack = onNavigateBack
            )
        }

        detailScreen(
            navController = navController,
            routeBase = Routes.FLIGHT_DETAILS
        ) { tripId, onNavigate, onNavigateBack ->
            FlightScreen(
                tripId = tripId,
                onNavigate = onNavigate,
                onNavigateBack = onNavigateBack
            )
        }

        detailScreen(
            navController = navController,
            routeBase = Routes.HOTEL_DETAILS
        ) { tripId, onNavigate, onNavigateBack ->
            HotelScreen(
                tripId = tripId,
                onNavigate = onNavigate,
                onNavigateBack = onNavigateBack
            )
        }

        detailScreen(
            navController = navController,
            routeBase = Routes.BUDGET_DETAILS
        ) { tripId, onNavigate, onNavigateBack ->
            BudgetScreen(
                tripId = tripId,
                onNavigate = onNavigate,
                onNavigateBack = onNavigateBack
            )
        }

    }
}

/**
 * Helper extension that registers a `routeBase/{tripId}` composable with a Long `tripId`
 * and provides common navigation behavior to the screen lambda.
 */
private fun NavGraphBuilder.detailScreen(
    navController: NavHostController,
    routeBase: String,
    content: @Composable (tripId: Long, onNavigate: (String) -> Unit, onNavigateBack: () -> Unit) -> Unit
) {
    composable(
        route = "$routeBase/{tripId}",
        arguments = listOf(navArgument("tripId") { type = NavType.LongType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getLong("tripId")
        if (tripId != null) {
            content(
                tripId,
                { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                { navController.popBackStack() }
            )
        }
    }
}