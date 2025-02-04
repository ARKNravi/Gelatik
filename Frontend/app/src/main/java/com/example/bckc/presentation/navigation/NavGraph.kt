package com.example.bckc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bckc.presentation.screens.auth.LoginScreen
import com.example.bckc.presentation.screens.forum.ForumScreen
import com.example.bckc.presentation.screens.home.HomeScreen
import com.example.bckc.presentation.screens.jbi.JBIScreen
import com.example.bckc.presentation.screens.profile.ProfileScreen
import com.example.bckc.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun NavGraph(
    navController: NavHostController,
    preferenceManager: PreferenceManager
) {
    val startDestination = if (preferenceManager.getToken() != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, currentRoute)
        }
        composable(Screen.Forum.route) {
            ForumScreen(navController)
        }
        composable(Screen.JBI.route) {
            JBIScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }

    // Handle token expiration
    LaunchedEffect(Unit) {
        if (preferenceManager.getToken() == null && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
