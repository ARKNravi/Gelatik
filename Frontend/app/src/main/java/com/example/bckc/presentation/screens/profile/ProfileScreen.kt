package com.example.bckc.presentation.screens.profile

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.navigation.Screen
import com.example.bckc.presentation.components.NavigationBar
import com.example.bckc.presentation.screens.profile.viewmodel.ProfileViewModel
import com.example.bckc.utils.Resource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var selectedTab by remember { mutableStateOf(2) } // 0: Cerita Anda, 1: Tersimpan, 2: Tandai

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Background Image (kotak.png)
            Image(
                painter = painterResource(id = R.drawable.kotak),
                contentDescription = "Background Pattern",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)  // Increased height to cover from top to tabs
            )

            // Content overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Top Bar with Title and Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profil Pengguna",
                        fontSize = 20.sp,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Profile Info Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Profile Content
                    when (profileState) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                color = Color(0xFF4A90E2)
                            )
                        }
                        is Resource.Success -> {
                            val user = (profileState as Resource.Success).data
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile Picture
                                if (user.profilePictureUrl != null) {
                                    CoilImage(
                                        imageModel = { user.profilePictureUrl },
                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0E7FF))
                                    )
                                }

                                // User Info
                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = user.fullName,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1A1A1A),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = user.birthDate.replace("-", "/"),
                                        fontSize = 12.sp,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFF1A1A1A),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Teman ${user.identityType.replaceFirstChar { it.uppercase() }}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF1A1A1A)
                                        )
                                    }
                                }

                                // Edit Button
                                IconButton(
                                    onClick = { /* TODO: Handle edit */ },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = Color(0xFFE0E7FF),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit),
                                        contentDescription = "Edit Profile",
                                        tint = Color(0xFF1A1A1A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            Text(
                                text = (profileState as Resource.Error).message ?: "Unknown error",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        null -> {}
                    }
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TabItem("Cerita Anda", selectedTab == 0) { selectedTab = 0 }
            TabItem("Tersimpan", selectedTab == 1) { selectedTab = 1 }
            TabItem("Tandai", selectedTab == 2) { selectedTab = 2 }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFAFAFA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bookmark),
                    contentDescription = "No bookmarks",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada cerita yang ditandai.",
                    color = Color(0xFF8C8C8C),
                    fontSize = 16.sp
                )
            }
        }

        // Navigation Bar
        NavigationBar(
            navController = navController,
            currentRoute = Screen.Profile.route,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF4A90E2) else Color(0xFFBFBFBF),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(2.dp)
                    .background(Color(0xFF4A90E2))
            )
        }
    }
}
