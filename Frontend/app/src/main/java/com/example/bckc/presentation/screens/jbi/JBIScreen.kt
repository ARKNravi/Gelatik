package com.example.bckc.presentation.screens.jbi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bckc.R
import com.example.bckc.data.model.response.TranslationOrderResponse
import com.example.bckc.data.model.response.TranslatorResponse
import com.example.bckc.presentation.components.NavigationBar
import com.example.bckc.presentation.navigation.Screen
import com.example.bckc.presentation.screens.jbi.viewmodel.JBIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JBIScreen(
    navController: NavController,
    viewModel: JBIViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header with shadow
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 32.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Layanan JBI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1D28),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // Search and Tabs Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // Location Label
                Text(
                    text = "Masukkan lokasi anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1D28),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 8.dp)
                )

                // Location Search
                OutlinedTextField(
                    value = viewModel.searchQuery.value,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = {
                        Text(
                            "Ketik di sini...",
                            color = Color(0xFF8C8C8C)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF8C8C8C)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF2171CF)
                    )
                )

                // Tab Navigation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TabItem(
                            text = "Pilihan JBI",
                            isSelected = viewModel.selectedTab.value == 0,
                            onClick = { viewModel.onTabSelected(0) },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            text = "Riwayat",
                            isSelected = viewModel.selectedTab.value == 1,
                            onClick = { viewModel.onTabSelected(1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Content based on selected tab
            when (viewModel.selectedTab.value) {
                0 -> {
                    // JBI List Tab
                    when {
                        viewModel.isLoading.collectAsState().value -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF2171CF))
                            }
                        }
                        viewModel.error.collectAsState().value != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = viewModel.error.collectAsState().value ?: "An error occurred",
                                    color = Color.Red
                                )
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                items(viewModel.getFilteredTranslators()) { translator ->
                                    TranslatorCard(
                                        translator = translator,
                                        onClick = { /* Handle click */ }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    @Composable
                    fun HistoryContent() {
                        when {
                            viewModel.isLoading.collectAsState().value -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF2171CF))
                                }
                            }
                            viewModel.error.collectAsState().value != null -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = viewModel.error.collectAsState().value ?: "An error occurred",
                                        color = Color.Red
                                    )
                                }
                            }
                            viewModel.translationOrders.collectAsState().value.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No riwayat available",
                                        fontSize = 16.sp,
                                        color = Color(0xFF8C8C8C),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            else -> {
                                val orders = viewModel.translationOrders.collectAsState().value
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(vertical = 16.dp)
                                ) {
                                    items(orders) { order ->
                                        TranslationOrderCard(
                                            order = order,
                                            onContactClick = { viewModel.onContactTranslator(order.id) },
                                            onCompleteClick = { viewModel.onCompleteOrder(order.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HistoryContent()
                }
            }
        }

        // Navigation Bar
        NavigationBar(
            navController = navController,
            currentRoute = Screen.JBI.route,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF2171CF) else Color(0xFF8C8C8C),
                modifier = Modifier.wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                // Grey line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFFE2E8F0))
                )
                // Blue line (only for selected tab)
                if (isSelected) {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Transparent,
                        modifier = Modifier
                            .background(Color(0xFF2171CF))
                            .height(2.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun TranslatorCard(
    translator: TranslatorResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with Badge
            Box {
                AsyncImage(
                    model = translator.profilePic.takeIf { !it.isNullOrEmpty() }
                        ?: R.drawable.ic_profile,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F1FF)),
                    contentScale = ContentScale.Crop
                )
                if (translator.availability) {
                    Icon(
                        painter = painterResource(id = R.drawable.reward),
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (4).dp, y = (6).dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = translator.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B1D28)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF8C8C8C),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = translator.alamat,
                        fontSize = 14.sp,
                        color = Color(0xFF8C8C8C)
                    )
                }
                if (translator.availability) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF59E0B)
                    ) {
                        Text(
                            text = "Available",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Arrow Button
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2171CF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "View Details",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun TranslationOrderCard(
    order: TranslationOrderResponse,
    onContactClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Avatar and info
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with badge
                Box(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = order.translator.profilePic?.takeIf { it.isNotEmpty() }
                            ?: R.drawable.ic_profile,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F1FF)),
                        contentScale = ContentScale.Crop
                    )
                    if (order.translator.availability) {
                        Icon(
                            painter = painterResource(id = R.drawable.reward),
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = 4.dp, y = 6.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                // Name and details
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.translator.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B1D28)
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE6FFF6)
                        ) {
                            Text(
                                text = "Approved",
                                color = Color(0xFF00D589),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Address
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = null,
                            tint = Color(0xFF8C8C8C),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = order.translator.alamat,
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C)
                        )
                    }

                    // Schedule
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.alarm),
                            contentDescription = null,
                            tint = Color(0xFF8C8C8C),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = order.timeSlot,
                            fontSize = 14.sp,
                            color = Color(0xFF8C8C8C)
                        )
                    }
                }
            }

            // Buttons in separate row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Contact Button
                Button(
                    onClick = onContactClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2171CF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.phone),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hubungi JBI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Complete Order Button
                OutlinedButton(
                    onClick = onCompleteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2171CF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2171CF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Pesanan Selesai",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}