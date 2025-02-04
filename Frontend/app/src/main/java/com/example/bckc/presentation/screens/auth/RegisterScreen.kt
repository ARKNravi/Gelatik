package com.example.bckc.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bckc.presentation.screens.auth.viewmodel.AuthViewModel
import com.example.bckc.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedIdentity by remember { mutableStateOf<String?>(null) }
    
    val isFormValid = fullName.isNotBlank() && 
                     birthDate.isNotBlank() && 
                     email.isNotBlank() && 
                     selectedIdentity != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Back button
        Box(
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp)
                .size(40.dp)
                .background(Color(0xFFE0E7FF), RoundedCornerShape(100.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color(0xFF144F93),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        // Title
        Text(
            text = "Daftar Akun",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF144F93),
            modifier = Modifier.padding(start = 26.dp, top = 24.dp)
        )

        // Login link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 26.dp, top = 8.dp)
        ) {
            Text(
                text = "Apakah kamu sudah memiliki akun?",
                fontSize = 14.sp,
                color = Color(0xFF8C8C8C)
            )
            Text(
                text = "Masuk",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFED9811),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { navController.navigate(Screen.Login.route) }
            )
        }

        // Form fields
        Text(
            text = "Nama Lengkap",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 26.dp, top = 32.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = { Text("Masukkan Nama Lengkap", color = Color(0xFFBFBFBF)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEAEAEA),
                focusedBorderColor = Color(0xFF144F93)
            )
        )

        Text(
            text = "Tanggal Lahir",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 26.dp, top = 20.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = birthDate,
            onValueChange = { input ->
                if (input.length <= 10) {
                    var formatted = input.filter { it.isDigit() }
                    if (formatted.length >= 4) {
                        formatted = formatted.substring(0, 2) + "/" + formatted.substring(2)
                    }
                    if (formatted.length >= 7) {
                        formatted = formatted.substring(0, 5) + "/" + formatted.substring(5)
                    }
                    birthDate = formatted
                }
            },
            placeholder = { Text("DD/MM/YYYY", color = Color(0xFFBFBFBF)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Calendar",
                    tint = Color(0xFF144F93)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEAEAEA),
                focusedBorderColor = Color(0xFF144F93)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text(
            text = "Email",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 26.dp, top = 20.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Masukkan Email", color = Color(0xFFBFBFBF)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEAEAEA),
                focusedBorderColor = Color(0xFF144F93)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Text(
            text = "Pilihan Identitas",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(start = 26.dp, top = 20.dp, bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            OutlinedButton(
                onClick = { selectedIdentity = "tuli" },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (selectedIdentity == "tuli") Color(0xFF144F93) else Color(0xFFBFBFBF)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedIdentity == "tuli") Color(0xFFE0E7FF) else Color.White
                )
            ) {
                Text(
                    text = "Tuli",
                    color = if (selectedIdentity == "tuli") Color(0xFF144F93) else Color(0xFFBFBFBF)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = { selectedIdentity = "dengar" },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (selectedIdentity == "dengar") Color(0xFF144F93) else Color(0xFFBFBFBF)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedIdentity == "dengar") Color(0xFFE0E7FF) else Color.White
                )
            ) {
                Text(
                    text = "Dengar",
                    color = if (selectedIdentity == "dengar") Color(0xFF144F93) else Color(0xFFBFBFBF)
                )
            }
        }

        // Continue button
        Button(
            onClick = {
                viewModel.updateRegistrationData(
                    email = email,
                    fullName = fullName,
                    birthDate = birthDate,
                    identityType = selectedIdentity ?: ""
                )
                try {
                    navController.navigate(Screen.RegisterPassword.route) {
                        popUpTo(Screen.Register.route) { inclusive = false }
                    }
                } catch (e: Exception) {
                    println("Navigation error: ${e.message}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFF4A90E2) else Color(0xFFD9D9D9),
                contentColor = if (isFormValid) Color.White else Color(0xFF8C8C8C)
            ),
            enabled = isFormValid
        ) {
            Text(
                text = "Lanjutkan",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Or register with
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF8C8C8C)
            )
            Text(
                text = "Atau Daftar dengan",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                color = Color(0xFF8C8C8C)
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF8C8C8C)
            )
        }

        // Google sign in button
        OutlinedButton(
            onClick = { /* TODO: Implement Google Sign In */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFBFBFBF)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google",
                    color = Color(0xFF1A1A1A),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
