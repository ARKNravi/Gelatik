package com.example.bckc.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.navigation.Screen
import com.example.bckc.utils.TokenManager
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    tokenManager: TokenManager,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F1FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1B1D28)
                )
            }
            
            Text(
                text = "Pengaturan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Account Section
            SettingsSection(
                title = "Akun",
                items = listOf(
                    SettingsItem(
                        icon = R.drawable.edit,
                        title = "Edit Profil",
                        subtitle = "Lakukan perubahan pada akun Anda",
                        onClick = { navController.navigate(Screen.EditProfile.route) }
                    ),
                    SettingsItem(
                        icon = R.drawable.settings,
                        title = "Keamanan",
                        subtitle = "Ubah kata sandi untuk keamanan akun",
                        onClick = { onNavigate(Screen.SecurityCheck.route) }
                    ),
                    SettingsItem(
                        icon = R.drawable.settings,
                        title = "Perizinan Aplikasi",
                        subtitle = "Personalisasi akun Anda",
                        onClick = { /* TODO: Implement permissions settings */ }
                    )
                )
            )

            // Help Center Section
            SettingsSection(
                title = "Pusat Bantuan",
                items = listOf(
                    SettingsItem(
                        icon = R.drawable.settings,
                        title = "Pusat Bantuan",
                        subtitle = "Hubungi Studeaf untuk bantuan",
                        onClick = { /* TODO: Implement help center */ }
                    ),
                    SettingsItem(
                        icon = R.drawable.settings,
                        title = "Ketentuan Layanan",
                        subtitle = "Aturan penggunaan dan kebijakan",
                        onClick = { navController.navigate(Screen.Terms.route) }
                    ),
                    SettingsItem(
                        icon = R.drawable.settings,
                        title = "Tentang Aplikasi",
                        subtitle = "Detail versi dan aplikasi",
                        onClick = { navController.navigate(Screen.About.route) }
                    )
                )
            )

            // Logout Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                SettingsMenuItem(
                    icon = R.drawable.settings,
                    title = "Keluar",
                    subtitle = "Keluar dari akun Anda",
                    onClick = {
                        tokenManager.clearToken()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsMenuItem(
                        icon = item.icon,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick
                    )
                    if (index < items.size - 1) {
                        Divider(
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE8F1FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color(0xFF2171CF),
                modifier = Modifier.size(24.dp)
            )
        }

        // Text
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }

        // Arrow
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF64748B)
        )
    }
}

private data class SettingsItem(
    val icon: Int,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)