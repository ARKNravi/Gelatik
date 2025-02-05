package com.example.bckc.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bckc.presentation.navigation.Screen
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun AuthScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(40.dp))
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(40.dp)
                )
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 192.dp, start = 27.dp, end = 27.dp)
                    .height(32.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {}
            }

            CoilImage(
                imageModel = { "https://figma-alpha-api.s3.us-west-2.amazonaws.com/images/c3b7d9b7-7e64-4aba-9dd7-9897704bc8bb" },
                imageOptions = ImageOptions(contentScale = ContentScale.Fit),
                modifier = Modifier
                    .padding(bottom = 36.dp, start = 57.dp, end = 57.dp)
                    .height(216.dp)
                    .fillMaxWidth()
            )

            Text(
                text = "Berhasil membuat akun!",
                color = Color(0xFF1F1F1F),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "Selamat akun Anda telah berhasil dibuat! Silakan masuk untuk dapat mengakses aplikasi.",
                color = Color(0xFF8C8C8C),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 37.dp, start = 36.dp, end = 36.dp)
            )

            Button(
                onClick = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(bottom = 214.dp, start = 23.dp, end = 23.dp)
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(
                        elevation = 5.dp,
                        spotColor = Color(0x0D000000)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text(
                    text = "Masuk",
                    color = Color(0xFFFFFFFF),
                    fontSize = 16.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(100.dp))
                        .width(142.dp)
                        .height(4.dp)
                        .background(
                            color = Color(0xFF363636),
                            shape = RoundedCornerShape(100.dp)
                        )
                )
            }
        }
    }
} 