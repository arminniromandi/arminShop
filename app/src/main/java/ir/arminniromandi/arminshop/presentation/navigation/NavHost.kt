package ir.arminniromandi.arminshop.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.arminniromandi.arminshop.presentation.ui.screens.CartScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.CategoryProductsScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.CategoryScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.HomeScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.LoginScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.ProductDetailScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.SearchScreen
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    cartViewModel: CartSharedViewModel
) {
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = ScreensRoute.Home.route,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(500)) + fadeIn(
                animationSpec = tween(150),
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(150))
        },
    ) {
        composable(ScreensRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ScreensRoute.Home.route) {
                        popUpTo(ScreensRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(ScreensRoute.Home.route) {
            HomeScreen(
                onSearch = { query ->
                    navController.navigate(ScreensRoute.Search.createRoute(query))
                },
                onProductClick = { product ->
                    navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                },
                onCategoryClick = { categoryId ->
                    navController.navigate(ScreensRoute.CategoryProducts.createRoute(categoryId))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                }
            )
        }
        composable(ScreensRoute.Categories.route) {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(ScreensRoute.CategoryProducts.createRoute(categoryId))
                }
            )
        }
        composable(ScreensRoute.Cart.route) {
            CartScreen(
                viewModel = cartViewModel,
                onNavigateToHome = {
                    navController.navigate(ScreensRoute.Home.route)
                }
            )
        }
        composable(ScreensRoute.Profile.route) {
            HomeScreen(
                onProductClick = { product ->
                    navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                }
            )
        }
        composable(
            route = ScreensRoute.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) {
            ProductDetailScreen(
                onProductClick = { product ->
                    navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                }
            )
        }
        composable(
            route = ScreensRoute.Search.route,
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            SearchScreen(
                onProductClick = { product ->
                    navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                }
            )
        }
        composable(
            route = ScreensRoute.CategoryProducts.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            CategoryProductsScreen(
                onProductClick = { product ->
                    navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                },
                onAddToCart = { product ->
                    cartViewModel.addToCart(product)
                }
            )
        }
    }
}
