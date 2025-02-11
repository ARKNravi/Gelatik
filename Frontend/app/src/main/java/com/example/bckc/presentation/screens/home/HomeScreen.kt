package com.example.bckc.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bckc.presentation.components.NavigationBar
import com.example.bckc.presentation.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    currentRoute: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Home Screen",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF144F93)
            )
        }
        
        NavigationBar(
            navController = navController,
            currentRoute = Screen.Home.route,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
