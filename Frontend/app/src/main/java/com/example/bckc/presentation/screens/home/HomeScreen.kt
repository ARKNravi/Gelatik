package com.example.bckc.presentation.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.components.NavigationBar
import com.example.bckc.presentation.navigation.Screen
import com.example.bckc.presentation.screens.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    currentRoute: String,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with shadow
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Belajar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1D28),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 16.dp)
                )
            }

            // Welcome Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C)
                        )
                        Text(
                            text = userProfile?.full_name ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1D28)
                        )
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2171CF)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Teman " + (userProfile?.identity_type?.replaceFirstChar { it.uppercase() } ?: ""),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Study Time Progress
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = 0.75f,
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF2171CF),
                            trackColor = Color(0xFFFFB74D)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "180",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B1D28)
                            )
                            Text(
                                text = "menit",
                                fontSize = 12.sp,
                                color = Color(0xFF8C8C8C)
                            )
                        }
                    }
                }
            }

            // Avatar and AutoCaption Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Avatar 3D dan AutoCaption",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1D28)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_3d),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = buildAnnotatedString {
                                append("Belajar lebih mudah dengan ")
                                withStyle(SpanStyle(color = Color(0xFF2171CF))) {
                                    append("Avatar penerjemah")
                                }
                                append(" dan ")
                                withStyle(SpanStyle(color = Color(0xFFFFB74D))) {
                                    append("Auto-Caption")
                                }
                                append("!")
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2171CF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Mulai Belajar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Summary Section
            Text(
                text = "Rangkuman",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D28),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5EB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Review materi dengan",
                            fontSize = 16.sp,
                            color = Color(0xFF1B1D28)
                        )
                        Text(
                            text = "rangkuman otomatis",
                            fontSize = 16.sp,
                            color = Color(0xFFFFB74D),
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2171CF)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Lihat Rangkuman")
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.reading_illustration),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Premium Section
            Text(
                text = "Ayo Berlangganan!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D28),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            PremiumFeaturesCard(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))
        }

        NavigationBar(
            navController = navController,
            currentRoute = Screen.Home.route,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun PremiumFeaturesCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left side - Illustration
            Image(
                painter = painterResource(id = R.drawable.premium_illustration),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            // Right side - Content
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Premium",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2171CF)
                )

                // Features list
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    PremiumFeatureItem("Akses komunitas tanpa batas")
                    PremiumFeatureItem("Perekaman medis tanpa batas")
                    PremiumFeatureItem("Bebas penambahan kontak SOS")
                }
            }
        }
    }
}

@Composable
private fun PremiumFeatureItem(
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = null,
            tint = Color(0xFFFFB74D),
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFF1B1D28),
            fontWeight = FontWeight.Medium
        )
    }
}
