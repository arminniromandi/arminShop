package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.arminniromandi.arminshop.presentation.components.ShopBottomNavigation
import ir.arminniromandi.arminshop.presentation.navigation.BottomNavItem
import ir.arminniromandi.arminshop.presentation.navigation.MainNavHost


@Composable
fun MainScreen(modifier: Modifier = Modifier) {


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        modifier = modifier.systemBarsPadding(),
        bottomBar = {
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
    ) {


        MainNavHost(
            navController = navController,
            innerPadding = it
        )

    }

}




