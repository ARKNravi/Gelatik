package com.example.bckc.presentation.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bckc.R

@Composable
fun AboutAppScreen(
    onBackClick: () -> Unit
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
                onClick = onBackClick,
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
                text = "Tentang Aplikasi",
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.studeaf_logo),
                contentDescription = "STUDEAF Logo",
                modifier = Modifier
                    .width(216.dp)
                    .height(100.dp)
                    .padding(vertical = 24.dp)
            )

            // Version
            Text(
                text = "Versi 1.0.0",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // App Description
            Section(
                title = "Deskripsi Aplikasi:",
                content = "StuDeaf adalah aplikasi inklusif yang dirancang untuk mendukung teman-teman Tuli dalam proses belajar. Dengan memanfaatkan teknologi avatar 3D Juru Bahasa Isyarat (JBI), subtitle otomatis, dan fitur rangkuman, aplikasi ini memberikan pengalaman belajar yang lebih interaktif dan mendalam."
            )

            // Contact Information
            Section(
                title = "Hubungi Kami:",
                content = """
                    Email: support@studeaf.com
                    Telepon: +62 123 456 789
                    Website: www.studeaf.com
                """.trimIndent()
            )

            // Privacy Policy & Terms
            Section(
                title = "Kebijakan Privasi & Ketentuan Layanan:",
                content = "Untuk informasi lebih lanjut, kunjungi menu Kebijakan Privasi dan Ketentuan Layanan di aplikasi."
            )

            // Copyright
            Section(
                title = "Hak Cipta:",
                content = "© 2025 StuDeaf. Semua Hak Dilindungi.",
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1D28),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = content,
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            lineHeight = 24.sp
        )
    }
} 