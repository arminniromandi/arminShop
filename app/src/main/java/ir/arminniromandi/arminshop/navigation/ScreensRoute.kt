package ir.arminniromandi.arminshop.navigation

sealed class ScreensRoute(val route : String){
    data object Login : ScreensRoute("login")


}

