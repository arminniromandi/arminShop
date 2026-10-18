package ir.arminniromandi.arminshop.presentation.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Switches to a bottom navigation tab.
 *
 * Each tab is a nested graph with its own back stack, so the state of the tab being left is saved
 * and the state of the tab being entered is restored.
 */
fun NavHostController.navigateToTab(route: String) {
    val startDestRoute = graph.findStartDestination().route
    val isStartDestination = route == startDestRoute || route == graph.startDestinationRoute

    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = !isStartDestination
    }
}
