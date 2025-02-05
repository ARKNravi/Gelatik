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
    // Get stored registration data
    val registrationData by viewModel.registrationData.collectAsState()
    
    // Initialize state with existing data
    var fullName by remember { mutableStateOf(registrationData.fullName) }
    var birthDate by remember { mutableStateOf(registrationData.birthDate) }
    var email by remember { mutableStateOf(registrationData.email) }
    var selectedIdentity by remember { mutableStateOf(registrationData.identityType) }

    // Error states
    var fullNameError by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var identityError by remember { mutableStateOf("") }

    // Email validation
    val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") }
    val isEmailValid = email.matches(emailPattern)

    // Validate form fields
    val isFormValid = fullName.isNotBlank() && 
                     birthDate.isNotBlank() && 
                     email.isNotBlank() && isEmailValid && 
                     selectedIdentity != null

    // Update registration data when fields change
    LaunchedEffect(fullName, birthDate, email, selectedIdentity) {
        viewModel.updateRegistrationData(
            email = email,
            fullName = fullName,
            birthDate = birthDate,
            identityType = selectedIdentity ?: ""
        )
    }

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
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Nama Lengkap",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = fullName,
                onValueChange = { 
                    fullName = it
                    fullNameError = if (it.isBlank()) "Nama tidak boleh kosong" else ""
                },
                placeholder = { Text("Masukkan Nama Lengkap", color = Color(0xFFBFBFBF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (fullNameError.isNotEmpty()) Color(0xFFF83B3F) else Color(0xFFEAEAEA),
                    focusedBorderColor = if (fullNameError.isNotEmpty()) Color(0xFFF83B3F) else Color(0xFF144F93)
                )
            )
            if (fullNameError.isNotEmpty()) {
                Text(
                    text = fullNameError,
                    color = Color(0xFFF83B3F),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "Tanggal Lahir",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
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
                        birthDateError = if (formatted.isBlank()) "Tanggal lahir tidak boleh kosong" else ""
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (birthDateError.isNotEmpty()) Color(0xFFF83B3F) else Color(0xFFEAEAEA),
                    focusedBorderColor = if (birthDateError.isNotEmpty()) Color(0xFFF83B3F) else Color(0xFF144F93)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (birthDateError.isNotEmpty()) {
                Text(
                    text = birthDateError,
                    color = Color(0xFFF83B3F),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "Email",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = when {
                        it.isBlank() -> "Email tidak boleh kosong"
                        !it.matches(emailPattern) -> "Format email tidak valid"
                        else -> ""
                    }
                },
                placeholder = { Text("Masukkan Email", color = Color(0xFFBFBFBF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = when {
                        emailError == "Format email tidak valid" -> Color(0xFFE9BA1F)
                        emailError.isNotEmpty() -> Color(0xFFF83B3F)
                        else -> Color(0xFFEAEAEA)
                    },
                    focusedBorderColor = when {
                        emailError == "Format email tidak valid" -> Color(0xFFE9BA1F)
                        emailError.isNotEmpty() -> Color(0xFFF83B3F)
                        else -> Color(0xFF144F93)
                    }
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = if (emailError == "Format email tidak valid") Color(0xFFE9BA1F) else Color(0xFFF83B3F),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

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
                // Save data before navigation
                viewModel.updateRegistrationData(
                    email = email,
                    fullName = fullName,
                    birthDate = birthDate,
                    identityType = selectedIdentity ?: ""
                )
                
                // Debug print to verify data is saved
                println("Saving data before navigation: email=$email, name=$fullName, birth=$birthDate, type=${selectedIdentity ?: ""}")
                
                navController.navigate(Screen.RegisterPassword.route)
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

        // Debug information
        Text(
            text = "Current Data: Email=$email, Name=$fullName, Birth=$birthDate, Identity=${selectedIdentity ?: ""}",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )

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
