package ir.arminniromandi.arminshop.presentation.navigation

enum class BottomNavItem(val title: String, val route: String) {
    HOME("خانه", ScreensRoute.Home.route),
    CATEGORIES("دسته بندی", ScreensRoute.Categories.route),
    CART("سبد خرید", ScreensRoute.Cart.route),
    PROFILE("پروفایل من", ScreensRoute.Profile.route)
}