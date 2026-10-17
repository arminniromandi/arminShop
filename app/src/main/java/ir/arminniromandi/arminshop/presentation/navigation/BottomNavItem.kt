package ir.arminniromandi.arminshop.presentation.navigation

import ir.arminniromandi.arminshop.R

enum class BottomNavItem(val title: String, val route: String ,val icon: Int) {
    HOME("خانه", ScreensRoute.Home.route , R.drawable.home_nav),
    CATEGORIES("دسته بندی", ScreensRoute.Categories.route , R.drawable.category_nav),
    CART("سبد خرید", ScreensRoute.Cart.route , R.drawable.shopping_nav),
    PROFILE("پروفایل من", ScreensRoute.Profile.route , R.drawable.user_nav)
}