package ir.arminniromandi.arminshop.presentation.navigation

import ir.arminniromandi.arminshop.R

/**
 * The bottom navigation tabs. Each tab points to a nested graph route (see [ScreensRoute]) so that
 * every tab keeps its own back stack and screen state.
 */
enum class BottomNavItem(val title: String, val route: String, val icon: Int) {
    HOME("خانه", ScreensRoute.HomeGraph.route, R.drawable.home_nav),
    CATEGORIES("دسته بندی", ScreensRoute.CategoriesGraph.route, R.drawable.category_nav),
    CART("سبد خرید", ScreensRoute.CartGraph.route, R.drawable.shopping_nav),
    PROFILE("پروفایل من", ScreensRoute.ProfileGraph.route, R.drawable.user_nav);

    companion object {
        /** Maps a graph route to its tab, or `null` when the route does not belong to a tab. */
        fun fromGraphRoute(route: String?): BottomNavItem? =
            entries.firstOrNull { it.route == route }
    }
}
