package com.example.bckc.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.navigation.Screen

@Composable
fun NavigationBar(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(Color.White)
    ) {
        // Top divider with gradient shadow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = Color(0x0A000000)
                )
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(
                icon = if (currentRoute == Screen.Home.route) R.drawable.teacherlight else R.drawable.teacher,
                label = "Belajar",
                isSelected = currentRoute == Screen.Home.route,
                onClick = { navController.navigate(Screen.Home.route) }
            )
            NavigationItem(
                icon = if (currentRoute == Screen.Forum.route) R.drawable.chattinglight else R.drawable.chatting,
                label = "Forum",
                isSelected = currentRoute == Screen.Forum.route,
                onClick = { navController.navigate(Screen.Forum.route) }
            )
            NavigationItem(
                icon = if (currentRoute == Screen.JBI.route) R.drawable.jbilight else R.drawable.jbi,
                label = "Layanan",
                isSelected = currentRoute == Screen.JBI.route,
                onClick = { navController.navigate(Screen.JBI.route) }
            )
            NavigationItem(
                icon = if (currentRoute == Screen.Profile.route) R.drawable.userlight else R.drawable.user,
                label = "Profil",
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { navController.navigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
private fun NavigationItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color(0xFF144F93) else Color(0xFF1F1F1F),
            style = TextStyle(fontSize = 12.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
} 