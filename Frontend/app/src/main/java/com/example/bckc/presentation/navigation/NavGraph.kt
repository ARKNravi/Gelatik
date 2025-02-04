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
import com.example.bckc.presentation.screens.auth.RegisterScreen
import com.example.bckc.presentation.screens.auth.RegisterPasswordScreen
import com.example.bckc.presentation.screens.forum.ForumScreen
import com.example.bckc.presentation.screens.home.HomeScreen
import com.example.bckc.presentation.screens.jbi.JBIScreen
import com.example.bckc.presentation.screens.profile.ProfileScreen
import com.example.bckc.utils.TokenManager

@Composable
fun NavGraph(
    navController: NavHostController,
    tokenManager: TokenManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        
        composable(route = Screen.RegisterPassword.route) {
            RegisterPasswordScreen(navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController, Screen.Home.route)
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
}
