package ir.arminniromandi.arminshop.presentation.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import ir.arminniromandi.arminshop.presentation.animation.LocalNavAnimatedVisibilityScope
import ir.arminniromandi.arminshop.presentation.animation.LocalSharedTransitionScope
import ir.arminniromandi.arminshop.presentation.ui.screens.auth.LoginScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.cart.CartScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.cart.CheckoutScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.cart.MyAddressesScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.category.CategoryProductsScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.category.CategoryScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.contact.ContactUsScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.experiences.MyExperiencesScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.home.HomeScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.notifications.NotificationsScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.orders.MyOrdersScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.product.ProductDetailScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.profile.EditProfileScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.profile.ProfileAddressesScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.profile.ProfileScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.search.SearchScreen
import ir.arminniromandi.arminshop.presentation.ui.screens.wishlist.WishlistScreen
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel
import ir.arminniromandi.arminshop.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    cartViewModel: CartSharedViewModel,
    startDestination: String = ScreensRoute.HomeGraph.route
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val user by profileViewModel.user.collectAsStateWithLifecycle()
    val userName = user?.fullName?.ifBlank { "آرمین نیرومندی" } ?: "آرمین نیرومندی"
    val phoneNumber = user?.phoneNumber?.ifBlank { "۰۹۳۰۰۳۱۸۲۳۹" } ?: "۰۹۳۰۰۳۱۸۲۳۹"

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = startDestination,
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

                // Product detail is reachable from home, search, category products, etc.
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

                // Checkout MyAddresses screen (dedicated for purchase selection)
                composable(ScreensRoute.MyAddresses.route) {
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        MyAddressesScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            isSelectionMode = true,
                            cartViewModel = cartViewModel
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
                                },
                                onNavigateToCheckout = {
                                    navController.navigate(ScreensRoute.Checkout.route)
                                }
                            )
                        }
                    }

                    composable(ScreensRoute.Checkout.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            CheckoutScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToAddresses = {
                                    navController.navigate(ScreensRoute.MyAddresses.route)
                                },
                                onNavigateToHome = {
                                    navController.navigateToTab(ScreensRoute.HomeGraph.route)
                                },
                                cartViewModel = cartViewModel
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
                    Log.i("test", "profile added")
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
                                    item.onClick(navController)
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

                    composable(ScreensRoute.MyOrders.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            MyOrdersScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                userName = userName,
                                phoneNumber = phoneNumber
                            )
                        }
                    }

                    composable(ScreensRoute.Wishlist.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            WishlistScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                userName = userName,
                                phoneNumber = phoneNumber,
                                onProductClick = { productId ->
                                    navController.navigate(ScreensRoute.ProductDetail.createRoute(productId))
                                },
                                onAddToCart = { product ->
                                    cartViewModel.addToCart(product)
                                }
                            )
                        }
                    }

                    composable(ScreensRoute.Notifications.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            NotificationsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                userName = userName,
                                phoneNumber = phoneNumber
                            )
                        }
                    }

                    composable(ScreensRoute.MyExperiences.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            MyExperiencesScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                userName = userName,
                                phoneNumber = phoneNumber
                            )
                        }
                    }

                    composable(ScreensRoute.ProfileAddresses.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            ProfileAddressesScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                userName = userName,
                                phoneNumber = phoneNumber
                            )
                        }
                    }

                    composable(ScreensRoute.ContactUs.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            ContactUsScreen(
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
