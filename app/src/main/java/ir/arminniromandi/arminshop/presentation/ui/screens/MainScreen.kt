package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.arminniromandi.arminshop.presentation.components.ShopBottomNavigation
import ir.arminniromandi.arminshop.presentation.components.ShopHeader
import ir.arminniromandi.arminshop.presentation.navigation.BottomNavItem
import ir.arminniromandi.arminshop.presentation.navigation.MainNavHost
import ir.arminniromandi.arminshop.presentation.navigation.ScreensRoute


@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoginRoute = currentRoute == ScreensRoute.Login.route

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        topBar = {
            if (!isLoginRoute) {
                ShopHeader(
                    onBackClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    onProfileClick = {
                        if (currentRoute != ScreensRoute.Profile.route) {
                            navController.navigate(ScreensRoute.Profile.route)
                        }
                    },
                    onNotificationClick = {
                        // Action for notifications
                    },
                    onMenuClick = {
                        // Action for menu
                    }
                )
            }
        },
        bottomBar = {
            if (!isLoginRoute) {
                ShopBottomNavigation(
                    selectedItem = BottomNavItem.entries.find { it.route == currentRoute } ?: BottomNavItem.HOME,
                    onItemSelected = {
                        val route = it.route
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        MainNavHost(
            navController = navController,
            innerPadding = innerPadding
        )

    }

}





