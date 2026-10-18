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
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import ir.arminniromandi.arminshop.presentation.animation.LocalNavAnimatedVisibilityScope
import ir.arminniromandi.arminshop.presentation.animation.LocalSharedTransitionScope
import ir.arminniromandi.arminshop.presentation.ui.screens.CartScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.EditProfileScreen
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
                startDestination = ScreensRoute.HomeGraph.route,
                enterTransition = { fadeIn(animationSpec = tween(350)) },
                exitTransition = { fadeOut(animationSpec = tween(350)) },
                popEnterTransition = { fadeIn(animationSpec = tween(350)) },
                popExitTransition = { fadeOut(animationSpec = tween(350)) },
            ) {
                // ---------------------------------------------------------------------------------
                // Root destinations (outside of the bottom navigation tabs)
                // ---------------------------------------------------------------------------------
                composable(ScreensRoute.Login.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(ScreensRoute.HomeGraph.route) {
                                    popUpTo(ScreensRoute.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                // Product detail is reachable from the home, search and category products
                // destinations, so it is registered once at the root of the graph.
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

                // ---------------------------------------------------------------------------------
                // Home tab
                // ---------------------------------------------------------------------------------
                navigation(
                    route = ScreensRoute.HomeGraph.route,
                    startDestination = ScreensRoute.Home.route
                ) {
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
                }

                // ---------------------------------------------------------------------------------
                // Categories tab
                // ---------------------------------------------------------------------------------
                navigation(
                    route = ScreensRoute.CategoriesGraph.route,
                    startDestination = ScreensRoute.Categories.route
                ) {
                    composable(ScreensRoute.Categories.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            CategoryScreen(
                                onCategoryClick = { categoryId ->
                                    navController.navigate(ScreensRoute.CategoryProducts.createRoute(categoryId))
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

                // ---------------------------------------------------------------------------------
                // Cart tab
                // ---------------------------------------------------------------------------------
                navigation(
                    route = ScreensRoute.CartGraph.route,
                    startDestination = ScreensRoute.Cart.route
                ) {
                    composable(ScreensRoute.Cart.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            CartScreen(
                                viewModel = cartViewModel,
                                onNavigateToHome = {
                                    navController.navigateToTab(ScreensRoute.HomeGraph.route)
                                }
                            )
                        }
                    }
                }

                // ---------------------------------------------------------------------------------
                // Profile tab
                // ---------------------------------------------------------------------------------
                navigation(
                    route = ScreensRoute.ProfileGraph.route,
                    startDestination = ScreensRoute.Profile.route
                ) {
                    composable(ScreensRoute.Profile.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            ProfileScreen(
                                onEditProfileClick = {
                                    navController.navigate(ScreensRoute.EditProfile.route)
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

                    composable(ScreensRoute.EditProfile.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            EditProfileScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
