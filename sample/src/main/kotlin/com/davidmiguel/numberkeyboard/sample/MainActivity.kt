package com.davidmiguel.numberkeyboard.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.davidmiguel.numberkeyboard.sample.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = { NavigationBar(navController) }) { innerPadding ->
                    NavHost(navController = navController, startDestination = BottomNavItem.Integer.route) {
                        composable(BottomNavItem.Integer.route) { IntegerScreen(innerPadding) }
                        composable(BottomNavItem.Decimal.route) { DecimalScreen(innerPadding) }
                        composable(BottomNavItem.Biometric.route) { BiometricScreen(innerPadding) }
                        composable(BottomNavItem.Custom.route) { CustomScreen(innerPadding) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        BottomNavItem.entries.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

enum class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
) {
    Integer(
        "Integer",
        "integer",
        Icons.Rounded.Numbers
    ),
    Decimal(
        "Decimal",
        "decimal",
        Icons.Rounded.AttachMoney
    ),
    Biometric(
        "Biometric",
        "biometric",
        Icons.Rounded.Fingerprint
    ),
    Custom(
        "Custom",
        "custom",
        Icons.Rounded.DashboardCustomize
    )
}