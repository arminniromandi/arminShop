package ir.arminniromandi.arminshop.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ir.arminniromandi.arminshop.presentation.ui.screens.HomeScreen


@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {

    //todo Impliment login first

    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = ScreensRoute.Home.route,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(500)) + fadeIn(
                animationSpec = tween(150)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(
                    500
                )
            ) + fadeOut(animationSpec = tween(150))
        },

    ) {
        composable(ScreensRoute.Home.route) {
            HomeScreen()
        }
        composable(ScreensRoute.Categories.route) {
            HomeScreen()
        }
        composable(ScreensRoute.Cart.route) {
            HomeScreen()
        }
        composable(ScreensRoute.Profile.route) {
            HomeScreen()
        }

    }
}



