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
import com.example.bckc.presentation.screens.auth.AuthScreen
import com.example.bckc.presentation.screens.forum.ForumScreen
import com.example.bckc.presentation.screens.home.HomeScreen
import com.example.bckc.presentation.screens.jbi.JBIScreen
import com.example.bckc.presentation.screens.profile.*
import com.example.bckc.presentation.screens.profile.viewmodel.ProfileViewModel
import com.example.bckc.utils.TokenManager
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
        
        composable(route = Screen.AuthScreen.route) {
            AuthScreen(navController)
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
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            SettingsScreen(
                navController = navController,
                tokenManager = profileViewModel.tokenManager,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Terms.route) {
            TermsOfServiceScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutAppScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SecurityCheck.route) {
            SecurityScreen(
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { /* TODO: Implement forgot password */ },
                onContinueClick = { navController.navigate(Screen.ChangePassword.route) }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onSaveClick = { newPassword, confirmPassword ->
                    // Handle save click if needed
                }
            )
        }
    }
}
