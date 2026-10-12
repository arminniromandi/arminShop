package ir.arminniromandi.arminshop.presentation.navigation

sealed class ScreensRoute(val route: String) {
    data object Login : ScreensRoute("login")
    data object Home : ScreensRoute("home")
    data object Categories : ScreensRoute("categories")
    data object Cart : ScreensRoute("cart")
    data object Profile : ScreensRoute("profile")
}


