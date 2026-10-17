package ir.arminniromandi.arminshop.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.arminniromandi.arminshop.presentation.animation.LocalNavAnimatedVisibilityScope
import ir.arminniromandi.arminshop.presentation.animation.LocalSharedTransitionScope
import ir.arminniromandi.arminshop.presentation.ui.screens.CartScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.category.CategoryProductsScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.category.CategoryScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.HomeScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.LoginScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.ProductDetailScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.ProfileScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.SearchScreen
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    cartViewModel: CartSharedViewModel
) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = ScreensRoute.Home.route,
                enterTransition = { fadeIn(animationSpec = tween(350)) },
                exitTransition = { fadeOut(animationSpec = tween(350)) },
                popEnterTransition = { fadeIn(animationSpec = tween(350)) },
                popExitTransition = { fadeOut(animationSpec = tween(350)) },
            ) {
                composable(ScreensRoute.Login.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(ScreensRoute.Home.route) {
                                    popUpTo(ScreensRoute.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
                composable(ScreensRoute.Home.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
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
                }
                composable(ScreensRoute.Categories.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        CategoryScreen(
                            onCategoryClick = { categoryId ->
                                navController.navigate(ScreensRoute.CategoryProducts.createRoute(categoryId))
                            }
                        )
                    }
                }
                composable(ScreensRoute.Cart.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        CartScreen(
                            viewModel = cartViewModel,
                            onNavigateToHome = {
                                navController.navigate(ScreensRoute.Home.route)
                            }
                        )
                    }
                }
                composable(ScreensRoute.Profile.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        ProfileScreen(
                            onEditProfileClick = {
                                // Action for edit profile
                            },
                            onChangeAvatarClick = {
                                // Action for change avatar
                            },
                            onMenuItemClick = { item ->
                                // Handle item click
                            }
                        )
                    }
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
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        ProductDetailScreen(
                            onProductClick = { product ->
                                navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                            },
                            onAddToCart = { product ->
                                cartViewModel.addToCart(product)
                            }
                        )
                    }
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
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        SearchScreen(
                            onProductClick = { product ->
                                navController.navigate(ScreensRoute.ProductDetail.createRoute(product.id))
                            },
                            onAddToCart = { product ->
                                cartViewModel.addToCart(product)
                            }
                        )
                    }
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
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
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
        }
    }
}
