package com.example.bckc.presentation.screens.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsOfServiceScreen(
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
                text = "Ketentuan Layanan",
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Section(
                title = "1. Pendahuluan",
                content = "Ketentuan Layanan ini mengatur penggunaan aplikasi kami. Dengan menggunakan aplikasi ini, Anda dianggap telah membaca, memahami, dan menyetujui seluruh syarat dan ketentuan yang berlaku."
            )

            Section(
                title = "2. Penggunaan Layanan",
                content = "Pengguna diharapkan mematuhi aturan berikut:",
                bulletPoints = listOf(
                    "Tidak menyalahgunakan fitur aplikasi untuk tindakan ilegal atau merugikan pihak lain.",
                    "Bertanggung jawab atas data dan informasi yang diunggah ke aplikasi."
                )
            )

            Section(
                title = "3. Hak dan Kewajiban Pengguna",
                content = "Pengguna memiliki hak untuk menggunakan semua fitur aplikasi sesuai dengan ketentuan. Sebagai pengguna, Anda berkewajiban:",
                bulletPoints = listOf(
                    "Menjaga kerahasiaan informasi akun Anda.",
                    "Tidak melanggar hak kekayaan intelektual pihak lain."
                )
            )

            Section(
                title = "4. Hak dan Kewajiban Penyedia Layanan",
                content = "Kami, sebagai penyedia layanan, bertanggung jawab untuk:",
                bulletPoints = listOf(
                    "Menyediakan layanan yang sesuai dengan deskripsi aplikasi.",
                    "Melindungi data pengguna sesuai dengan Kebijakan Privasi yang berlaku."
                )
            )

            Section(
                title = "5. Privasi dan Keamanan Data",
                content = "Kami berkomitmen menjaga privasi dan keamanan data Anda. Untuk informasi lebih lanjut, silakan baca Kebijakan Privasi kami yang tersedia di dalam aplikasi."
            )

            Section(
                title = "6. Pembatasan Tanggung Jawab",
                content = "Kami tidak bertanggung jawab atas kerugian yang timbul akibat:",
                bulletPoints = listOf(
                    "Penggunaan aplikasi di luar ketentuan layanan.",
                    "Gangguan teknis yang berada di luar kendali kami."
                )
            )

            Section(
                title = "7. Perubahan Ketentuan",
                content = "Kami berhak mengubah ketentuan layanan ini kapan saja. Perubahan akan diinformasikan melalui aplikasi, dan Anda disarankan untuk meninjau ketentuan layanan secara berkala."
            )

            Section(
                title = "8. Kontak",
                content = "Untuk pertanyaan lebih lanjut mengenai Ketentuan Layanan ini, Anda dapat menghubungi kami melalui email di support@example.com."
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: String,
    bulletPoints: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1D28),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = content,
            fontSize = 16.sp,
            color = Color(0xFF1B1D28),
            lineHeight = 24.sp
        )
        
        if (bulletPoints.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bulletPoints.forEach { point ->
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            fontSize = 16.sp,
                            color = Color(0xFF1B1D28)
                        )
                        Text(
                            text = point,
                            fontSize = 16.sp,
                            color = Color(0xFF1B1D28),
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
} 