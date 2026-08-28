package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DonghuaViewModel
import com.example.ui.Screen
import com.example.ui.components.AuthDialog
import com.example.ui.components.VipSubscriptionDialog
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LilacPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: DonghuaViewModel = viewModel()
                DonghuaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DonghuaApp(viewModel: DonghuaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val subscription by viewModel.userSubscription.collectAsState()
    val showVipDialog by viewModel.showVipDialog.collectAsState()
    val selectedPlan by viewModel.selectedVipPlan.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
    val paymentSuccessMessage by viewModel.paymentSuccessMessage.collectAsState()

    // Auth Dialog state
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val authMode by viewModel.authMode.collectAsState()
    val authEmail by viewModel.authEmail.collectAsState()
    val authPassword by viewModel.authPassword.collectAsState()
    val authName by viewModel.authName.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authErrorMessage by viewModel.authErrorMessage.collectAsState()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsState()
    val isResetOtpSent by viewModel.isResetOtpSent.collectAsState()

    // Active download count
    val activeDownloadsCount = downloads.count { it.status != com.example.data.model.DownloadStatus.COMPLETED }

    // Intercept Back Press when not on Home
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.goBack()
    }

    val isPlayerScreen = currentScreen is Screen.Player

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            AnimatedVisibility(
                visible = !isPlayerScreen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                DonghuaBottomNavigation(
                    currentScreen = currentScreen,
                    activeDownloadsCount = activeDownloadsCount,
                    isVip = subscription.isVip || currentUser.isOwner,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.History -> HistoryScreen(viewModel = viewModel)
                is Screen.Schedule -> ScheduleScreen(viewModel = viewModel)
                is Screen.Explore -> ExploreScreen(viewModel = viewModel)
                is Screen.Detail -> DetailScreen(donghuaId = screen.donghuaId, viewModel = viewModel)
                is Screen.Player -> PlayerScreen(
                    donghuaId = screen.donghuaId,
                    episodeId = screen.episodeId,
                    isOffline = screen.isOffline,
                    viewModel = viewModel
                )
                is Screen.Downloads -> DownloadsScreen(viewModel = viewModel)
                is Screen.Profile -> ProfileScreen(viewModel = viewModel)
            }

            // Global VIP Subscription Dialog
            if (showVipDialog) {
                VipSubscriptionDialog(
                    selectedPlan = selectedPlan,
                    selectedPaymentMethod = selectedPaymentMethod,
                    userExp = currentUser.exp,
                    discountPercent = currentUser.discountPercent,
                    isProcessing = isProcessingPayment,
                    successMessage = paymentSuccessMessage,
                    onPlanSelected = { viewModel.selectVipPlan(it) },
                    onPaymentMethodSelected = { viewModel.selectPaymentMethod(it) },
                    onConfirmPayment = { viewModel.processVipPayment() },
                    onDismiss = { viewModel.closeVipDialog() }
                )
            }

            // Global Auth Dialog (Login, Register, Lupa Password, Google Sign-In)
            if (showAuthDialog) {
                AuthDialog(
                    authMode = authMode,
                    email = authEmail,
                    password = authPassword,
                    name = authName,
                    isPasswordVisible = isPasswordVisible,
                    isLoading = isAuthenticating,
                    errorMessage = authErrorMessage,
                    successMessage = authSuccessMessage,
                    isResetOtpSent = isResetOtpSent,
                    onModeChange = { viewModel.setAuthMode(it) },
                    onEmailChange = { viewModel.setAuthEmail(it) },
                    onPasswordChange = { viewModel.setAuthPassword(it) },
                    onNameChange = { viewModel.setAuthName(it) },
                    onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                    onLoginClick = { viewModel.loginWithEmail() },
                    onRegisterClick = { viewModel.registerWithEmail() },
                    onGoogleLoginClick = { viewModel.loginWithGoogle() },
                    onSendResetEmailClick = { viewModel.sendPasswordResetEmail() },
                    onDismiss = { viewModel.closeAuthDialog() }
                )
            }
        }
    }
}

@Composable
fun DonghuaBottomNavigation(
    currentScreen: Screen,
    activeDownloadsCount: Int,
    isVip: Boolean,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_navigation_bar")
    ) {
        val navItems = listOf(
            NavItem(
                screen = Screen.Home,
                title = "Beranda",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_home"
            ),
            NavItem(
                screen = Screen.History,
                title = "Riwayat",
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History,
                testTag = "nav_history"
            ),
            NavItem(
                screen = Screen.Explore,
                title = "Jelajah",
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                testTag = "nav_explore"
            ),
            NavItem(
                screen = Screen.Downloads,
                title = "Unduhan",
                selectedIcon = Icons.Filled.Download,
                unselectedIcon = Icons.Outlined.Download,
                badgeCount = activeDownloadsCount,
                testTag = "nav_downloads"
            ),
            NavItem(
                screen = Screen.Profile,
                title = "Profil & VIP",
                selectedIcon = if (isVip) Icons.Filled.WorkspacePremium else Icons.Filled.Person,
                unselectedIcon = if (isVip) Icons.Outlined.WorkspacePremium else Icons.Outlined.Person,
                isVipHighlight = isVip,
                testTag = "nav_profile"
            )
        )

        navItems.forEach { item ->
            val isSelected = currentScreen::class == item.screen::class

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = GoldPrimary,
                                    contentColor = Color.Black
                                ) {
                                    Text(text = item.badgeCount.toString(), fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = if (item.isVipHighlight) GoldPrimary else if (isSelected) GoldPrimary else TextSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (item.isVipHighlight) GoldPrimary else if (isSelected) GoldPrimary else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LilacPrimary,
                    selectedTextColor = LilacPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Color(0xFF4A4458)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

data class NavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0,
    val isVipHighlight: Boolean = false,
    val testTag: String
)
