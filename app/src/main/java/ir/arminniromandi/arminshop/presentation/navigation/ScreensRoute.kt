package ir.arminniromandi.arminshop.presentation.navigation

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * All destinations of the app.
 *
 * Destinations are grouped into nested graphs so that every bottom navigation tab owns its own
 * back stack:
 *
 *  - [HomeGraph]        -> [Home] (start), [Search]
 *  - [CategoriesGraph]  -> [Categories] (start), [CategoryProducts]
 *  - [CartGraph]        -> [Cart] (start)
 *  - [ProfileGraph]     -> [Profile] (start)
 *
 * Destinations that are reachable from more than one tab (currently [ProductDetail], and [Login]
 * which lives outside the tab structure entirely) stay at the root of the graph so they are not
 * duplicated inside several tabs.
 */
sealed class ScreensRoute(val route: String) {

    // ---------------------------------------------------------------------------------------------
    // Graph routes (one per bottom navigation tab)
    // ---------------------------------------------------------------------------------------------

    data object HomeGraph : ScreensRoute("home_graph")
    data object CategoriesGraph : ScreensRoute("categories_graph")
    data object CartGraph : ScreensRoute("cart_graph")
    data object ProfileGraph : ScreensRoute("profile_graph")

    // ---------------------------------------------------------------------------------------------
    // Root destinations (outside of the bottom navigation tabs)
    // ---------------------------------------------------------------------------------------------

    data object Login : ScreensRoute("login")

    /** Shared by the Home, Search and CategoryProducts destinations, therefore kept at the root. */
    data object ProductDetail : ScreensRoute("product_detail/{productId}") {
        fun createRoute(productId: Int): String = "product_detail/$productId"
    }

    // ---------------------------------------------------------------------------------------------
    // Home tab
    // ---------------------------------------------------------------------------------------------

    data object Home : ScreensRoute("home")

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

    // ---------------------------------------------------------------------------------------------
    // Categories tab
    // ---------------------------------------------------------------------------------------------

    data object Categories : ScreensRoute("categories")

    data object CategoryProducts : ScreensRoute("category_products/{categoryId}") {
        fun createRoute(categoryId: String): String = "category_products/$categoryId"
    }

    // ---------------------------------------------------------------------------------------------
    // Cart tab
    // ---------------------------------------------------------------------------------------------

    data object Cart : ScreensRoute("cart")
    data object Checkout : ScreensRoute("checkout")
    data object MyAddresses : ScreensRoute("my_addresses")

    // ---------------------------------------------------------------------------------------------
    // Profile tab
    // ---------------------------------------------------------------------------------------------

    data object Profile : ScreensRoute("profile")

    data object EditProfile : ScreensRoute("edit_profile")
}
