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
import androidx.compose.ui.platform.LocalDensity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    var selectedTab by remember { mutableStateOf(2) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (profileState) {
                is Resource.Loading, null -> {
                    // Full screen loading
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2171CF),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                is Resource.Success -> {
                    // Main content
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // Background with curved bottom - starts from top
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .offset(y = (-70).dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.kotak),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Fixed Header Bar with Shadow
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White.copy(alpha = 0.95f),
                                    shadowElevation = 4.dp,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Profil Pengguna",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B1D28)
                                        )
                                        IconButton(
                                            onClick = { /* Handle settings */ }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.settings),
                                                contentDescription = "Settings",
                                                tint = Color(0xFF1B1D28),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                // Profile Info
                                Spacer(modifier = Modifier.height(24.dp))
                                val user = (profileState as Resource.Success).data
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Profile Picture
                                    Box(
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        if (user.profilePictureUrl != null) {
                                            CoilImage(
                                                imageModel = { user.profilePictureUrl },
                                                imageOptions = ImageOptions(
                                                    contentScale = ContentScale.Crop,
                                                    alignment = Alignment.Center
                                                ),
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        width = 3.dp,
                                                        color = Color(0xFF2171CF),
                                                        shape = CircleShape
                                                    )
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(id = R.drawable.avatar1),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        width = 3.dp,
                                                        color = Color(0xFF2171CF),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = user.fullName,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B1D28)
                                        )
                                        Text(
                                            text = user.email,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1B1D28)
                                        )
                                        Text(
                                            text = user.birthDate.replace("-", "/"),
                                            fontSize = 14.sp,
                                            color = Color(0xFF1B1D28)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = Color(0xFF2171CF).copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Teman ${user.identityType.replaceFirstChar { it.uppercase() }}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF2171CF),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Edit Button
                                    IconButton(
                                        onClick = { navController.navigate(Screen.EditProfile.route) },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = Color(0xFF2171CF),
                                                shape = CircleShape
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.edit),
                                            contentDescription = "Edit",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Tabs section
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TabItem("Cerita Anda", selectedTab == 0) { selectedTab = 0 }
                                        TabItem("Tersimpan", selectedTab == 1) { selectedTab = 1 }
                                        TabItem("Tandai", selectedTab == 2) { selectedTab = 2 }
                                    }
                                    // Full-width gray line
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFEEEEEE),
                                        thickness = 2.dp
                                    )
                                }
                            }
                        }

                        // Content Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.White)
                        ) {
                            when (profileState) {
                                is Resource.Loading, null -> {
                                    // Show loading indicator
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color(0xFF2171CF),
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }
                                is Resource.Success -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.bookmark),
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp),
                                            colorFilter = ColorFilter.tint(Color(0xFFCEDCF8))
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            text = when (selectedTab) {
                                                0 -> "Belum ada cerita yang dibuat."
                                                1 -> "Belum ada cerita yang disimpan."
                                                else -> "Belum ada cerita yang ditandai."
                                            },
                                            color = Color(0xFF8C8C8C),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                is Resource.Error -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = (profileState as Resource.Error).message ?: "Terjadi kesalahan",
                                            color = Color(0xFF1B1D28),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 32.dp)
                                        )
                                        TextButton(
                                            onClick = { viewModel.fetchUserProfile() },
                                            modifier = Modifier.padding(top = 8.dp)
                                        ) {
                                            Text(
                                                text = "Coba Lagi",
                                                color = Color(0xFF2171CF),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    // Full screen error state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = (profileState as Resource.Error).message ?: "Terjadi kesalahan",
                                color = Color(0xFF1B1D28),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            TextButton(
                                onClick = { viewModel.fetchUserProfile() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Coba Lagi",
                                    color = Color(0xFF2171CF),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation Bar (always visible)
        NavigationBar(
            navController = navController,
            currentRoute = Screen.Profile.route
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFF2171CF) else Color(0xFF8C8C8C),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Box(
                modifier = Modifier.height(2.dp)
            ) {
                // Gray line (base)
                Box(
                    modifier = Modifier
                        .width(text.length * 7.dp)
                        .height(2.dp)
                        .background(Color(0xFFEEEEEE))
                )
                
                // Blue indicator (overlay) if selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(text.length * 7.dp)
                            .height(2.dp)
                            .background(Color(0xFF2171CF))
                    )
                }
            }
        }
    }
}
