package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.arminniromandi.arminshop.presentation.components.AppNavigationDrawer
import ir.arminniromandi.arminshop.presentation.components.ShopBottomNavigation
import ir.arminniromandi.arminshop.presentation.components.ShopHeader
import ir.arminniromandi.arminshop.presentation.components.TopCartNotificationBanner
import ir.arminniromandi.arminshop.presentation.navigation.BottomNavItem
import ir.arminniromandi.arminshop.presentation.navigation.MainNavHost
import ir.arminniromandi.arminshop.presentation.navigation.ScreensRoute
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    cartViewModel: CartSharedViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val cartTotalCount by cartViewModel.cartTotalCount.collectAsStateWithLifecycle()
    val notificationMessage by cartViewModel.topNotificationMessage.collectAsStateWithLifecycle()

    val isLoginRoute = currentRoute == ScreensRoute.Login.route

    AppNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isLoginRoute,
        onProfileClick = {
            if (currentRoute != ScreensRoute.Profile.route) {
                navController.navigate(ScreensRoute.Profile.route)
            }
        },
        onCloseDrawer = {
            coroutineScope.launch { drawerState.close() }
        }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                topBar = {
                    if (!isLoginRoute) {
                        ShopHeader(
                            onBackClick = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            },
                            onProfileClick = {
                                if (currentRoute != ScreensRoute.Profile.route) {
                                    navController.navigate(ScreensRoute.Profile.route)
                                }
                            },
                            onNotificationClick = {
                                // Action for notifications
                            },
                            onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        drawerState.open()
                                    }
                                }
                            },
                            isDrawerOpen = drawerState.isOpen
                        )
                    }
                },
                bottomBar = {
                    if (!isLoginRoute) {
                        ShopBottomNavigation(
                            selectedItem = BottomNavItem.entries.find { it.route == currentRoute } ?: BottomNavItem.HOME,
                            cartBadgeCount = cartTotalCount,
                            onItemSelected = {
                                val route = it.route
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                MainNavHost(
                    navController = navController,
                    innerPadding = innerPadding,
                    cartViewModel = cartViewModel
                )
            }

            // Floating Top Notification Toast/Banner
            if (!isLoginRoute) {
                TopCartNotificationBanner(
                    message = notificationMessage,
                    onDismiss = { cartViewModel.dismissNotification() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .systemBarsPadding()
                )
            }
        }
    }
}
