package ir.arminniromandi.arminshop.presentation.navigation

import java.net.URLDecoder
import java.net.URLEncoder

sealed class ScreensRoute(val route: String) {
    data object Login : ScreensRoute("login")
    data object Home : ScreensRoute("home")
    data object Categories : ScreensRoute("categories")
    data object Cart : ScreensRoute("cart")
    data object Profile : ScreensRoute("profile")
    data object ProductDetail : ScreensRoute("product_detail/{productId}") {
        fun createRoute(productId: Int): String = "product_detail/$productId"
    }
    data object Search : ScreensRoute("search?query={query}") {
        fun createRoute(query: String = ""): String {
            val encodedQuery = try {
                URLEncoder.encode(query, "UTF-8")
            } catch (e: Exception) {
                query
            }
            return "search?query=$encodedQuery"
        }

        fun parseQuery(rawQuery: String?): String {
            if (rawQuery.isNullOrBlank()) return ""
            return try {
                URLDecoder.decode(rawQuery, "UTF-8")
            } catch (e: Exception) {
                rawQuery
            }
        }
    }
}
