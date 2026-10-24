package ir.arminniromandi.arminshop.utils

import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.presentation.model.CategoryItem
import ir.arminniromandi.arminshop.presentation.model.ProfileMenuItem
import ir.arminniromandi.arminshop.presentation.navigation.ScreensRoute

object Constance {

    /**
     * Base URL for Backend API
     * For Android Emulator use: "http://10.0.2.2:8080/api/"
     * For local PC browser/testing use: "http://localhost:8080/api/"
     * For physical device use: "http://<YOUR_LAN_IP>:8080/api/"
     */
    const val BASE_URL = "http://10.0.2.2:8080/api/"


    val categories = listOf(
        CategoryItem("1", "زنانه", R.drawable.woman_category),
        CategoryItem("2", "مردانه", R.drawable.man_category),
        CategoryItem("3", "کفش", R.drawable.shoes_category),
        CategoryItem("4", "بچه گانه", R.drawable.baby_category),
    )

    val profileMenuItems = listOf(
        ProfileMenuItem(
            title = "سفارشات من",
            iconRes = R.drawable.shopping_cart,
            onClick = { navController ->
                navController.navigate(ScreensRoute.MyOrders.route)
            }
        ),
        ProfileMenuItem(
            title = "تجربه های خرید من",
            iconRes = R.drawable.exprience,
            onClick = { navController ->
                navController.navigate(ScreensRoute.MyExperiences.route)
            }
        ),
        ProfileMenuItem(
            title = "اعلانات من",
            iconRes = R.drawable.notification,
            onClick = { navController ->
                navController.navigate(ScreensRoute.Notifications.route)
            }
        ),
        ProfileMenuItem(
            title = "علاقه مندی های من",
            iconRes = R.drawable.bookmark,
            onClick = { navController ->
                navController.navigate(ScreensRoute.Wishlist.route)
            }
        ),
        ProfileMenuItem(
            title = "آدرس های من",
            iconRes = R.drawable.location,
            onClick = { navController ->
                navController.navigate(ScreensRoute.ProfileAddresses.route)
            }
        )
    )
}
