package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.arminniromandi.arminshop.presentation.components.ShopBottomNavigation
import ir.arminniromandi.arminshop.presentation.components.ShopHeader
import ir.arminniromandi.arminshop.presentation.components.TopCartNotificationBanner
import ir.arminniromandi.arminshop.presentation.components.TopMenuDropdown
import ir.arminniromandi.arminshop.presentation.navigation.BottomNavItem
import ir.arminniromandi.arminshop.presentation.navigation.MainNavHost
import ir.arminniromandi.arminshop.presentation.navigation.ScreensRoute
import ir.arminniromandi.arminshop.presentation.navigation.navigateToTab
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel
import ir.arminniromandi.arminshop.presentation.viewmodels.LoginViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    cartViewModel: CartSharedViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(loginUiState.isLoggedIn) {
        if (loginUiState.isLoggedIn == false) {
            navController.navigate(ScreensRoute.Login.route) {
                popUpTo(ScreensRoute.HomeGraph.route) { inclusive = true }
            }
        }
    }

    var isMenuOpen by remember { mutableStateOf(false) }

    val cartTotalCount by cartViewModel.cartTotalCount.collectAsStateWithLifecycle()
    val notificationMessage by cartViewModel.topNotificationMessage.collectAsStateWithLifecycle()

    val isLoginRoute = currentRoute == ScreensRoute.Login.route

    // Each tab is a nested graph, so the selected tab is the one whose graph route shows up in the
    // hierarchy of the current destination. Shared destinations (e.g. product detail) live outside
    // the tabs and therefore fall back to the home tab.
    val selectedBottomNavItem = currentDestination
        ?.hierarchy
        ?.firstNotNullOfOrNull { BottomNavItem.fromGraphRoute(it.route) }
        ?: BottomNavItem.HOME

    BackHandler(enabled = isMenuOpen) {
        isMenuOpen = false
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {


        Box(
            modifier = modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(Color(0xFFFFF6EE))
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color(0xFFFFF6EE),
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
                                    navController.navigateToTab(ScreensRoute.ProfileGraph.route)
                                }
                            },
                            onNotificationClick = {
                                // Action for notifications
                            },
                            onMenuClick = {
                                isMenuOpen = !isMenuOpen
                            },
                            isDrawerOpen = isMenuOpen
                        )
                    }
                },
                bottomBar = {
                    if (!isLoginRoute) {
                        ShopBottomNavigation(
                            selectedItem = selectedBottomNavItem,
                            cartBadgeCount = cartTotalCount,
                            onItemSelected = { item ->
                                if (selectedBottomNavItem != item) {
                                    navController.navigateToTab(item.route)
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    MainNavHost(
                        navController = navController,
                        innerPadding = innerPadding,
                        cartViewModel = cartViewModel
                    )

                    // Top sliding menu dropdown positioned right under topBar
                    if (!isLoginRoute) {
                        TopMenuDropdown(
                            isOpen = isMenuOpen,
                            onDismissRequest = { isMenuOpen = false },
                            onProfileClick = {
                                if (currentRoute != ScreensRoute.Profile.route) {
                                    navController.navigateToTab(ScreensRoute.ProfileGraph.route)
                                }
                            },
                            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                        )
                    }
                }
            }

            // Floating Top Notification Toast/Banner
            if (!isLoginRoute) {
                TopCartNotificationBanner(
                    message = notificationMessage,
                    onDismiss = { cartViewModel.dismissNotification() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}