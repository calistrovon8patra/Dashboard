package com.github.dashboardapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.dashboardapp.ui.home.HomeScreen
import com.github.dashboardapp.ui.planner.PlannerScreen

// Define our navigation routes
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Today", Icons.Default.Today)
    object Planner : Screen("planner", "Planner", Icons.Default.DateRange)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Planner,
    Screen.Analytics,
)

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Planner.route) { PlannerScreen() }
            composable(Screen.Analytics.route) {
                // Placeholder for Analytics screen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Analytics Screen - Coming Soon!")
                }
            }
        }
    }
}
