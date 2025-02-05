package com.example.bckc.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bckc.R
import com.example.bckc.presentation.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bckc.presentation.screens.auth.viewmodel.AuthViewModel
import com.example.bckc.utils.Resource
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Get stored registration data
    val registrationData by viewModel.registrationData.collectAsState()
    val registerState by viewModel.registerState.collectAsState()

    // Password validation checks
    val hasMinLength = password.length >= 8
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val passwordsMatch = password == confirmPassword && password.isNotEmpty()

    val isFormValid = hasMinLength && hasLowerCase && hasUpperCase && 
                     hasDigit && hasSpecialChar && passwordsMatch && isTermsAccepted

    // Handle registration response
    LaunchedEffect(registerState) {
        when (registerState) {
            is Resource.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Registrasi berhasil!")
                }
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is Resource.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        when ((registerState as Resource.Error).message) {
                            "EMAIL_EXISTS" -> "Email sudah terdaftar"
                            "All fields must be filled" -> "Semua field harus diisi"
                            else -> (registerState as Resource.Error).message ?: "Terjadi kesalahan"
                        }
                    )
                }
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                text = "Kata Sandi Akun",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF144F93),
                modifier = Modifier.padding(start = 26.dp, top = 24.dp)
            )

            Text(
                text = "Masukkan kata sandi untuk keamanan akun Anda.",
                fontSize = 14.sp,
                color = Color(0xFF8C8C8C),
                modifier = Modifier.padding(start = 26.dp, top = 8.dp)
            )

            // Password field
            Text(
                text = "Kata Sandi",
                fontSize = 14.sp,
                color = Color(0xFF595959),
                modifier = Modifier.padding(start = 26.dp, top = 32.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Masukkan Kata Sandi", color = Color(0xFFBFBFBF)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (showPassword) R.drawable.eyeopen else R.drawable.eyeclose
                        ),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showPassword = !showPassword }
                    )
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEAEAEA),
                    focusedBorderColor = Color(0xFF144F93)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Password requirements
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PasswordRequirement(
                        text = "Minimal 8 karakter",
                        isMet = hasMinLength
                    )
                    PasswordRequirement(
                        text = "Karakter huruf kecil",
                        isMet = hasLowerCase
                    )
                    PasswordRequirement(
                        text = "Karakter angka",
                        isMet = hasDigit
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    PasswordRequirement(
                        text = "Karakter unik (!@#$)",
                        isMet = hasSpecialChar
                    )
                    PasswordRequirement(
                        text = "Karakter huruf besar",
                        isMet = hasUpperCase
                    )
                }
            }

            // Confirm Password field
            Text(
                text = "Konfirmasi Kata Sandi",
                fontSize = 14.sp,
                color = Color(0xFF595959),
                modifier = Modifier.padding(start = 26.dp, top = 16.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Masukkan Konfirmasi Kata Sandi", color = Color(0xFFBFBFBF)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (showConfirmPassword) R.drawable.eyeopen else R.drawable.eyeclose
                        ),
                        contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showConfirmPassword = !showConfirmPassword }
                    )
                },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFEAEAEA),
                    focusedBorderColor = Color(0xFF144F93)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Terms and conditions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .clickable { isTermsAccepted = !isTermsAccepted }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isTermsAccepted) R.drawable.checkbox_checked else R.drawable.checkbox_unchecked
                    ),
                    contentDescription = "Accept terms",
                    tint = if (isTermsAccepted) Color(0xFF79E6C0) else Color(0xFF8C8C8C),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Saya telah memahami dan setuju dengan syarat dan ketentuan serta kebijakan privasi",
                    fontSize = 12.sp,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Register button
            Button(
                onClick = {
                    if (isFormValid) {
                        // Format birth date from DD/MM/YYYY to YYYY-MM-DD
                        val parts = registrationData.birthDate.split("/")
                        val formattedDate = if (parts.size == 3) {
                            "${parts[2]}-${parts[1]}-${parts[0]}"
                        } else {
                            registrationData.birthDate
                        }

                        // Update the registration data with the password while preserving existing data
                        viewModel.updateRegistrationData(
                            email = registrationData.email,  // Preserve existing email
                            fullName = registrationData.fullName,  // Preserve existing fullName
                            birthDate = registrationData.birthDate,  // Preserve existing birthDate
                            identityType = registrationData.identityType,  // Preserve existing identityType
                            password = password,  // Add new password
                            passwordConfirm = confirmPassword  // Add new passwordConfirm
                        )

                        // For debugging, show the data that will be sent
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Sending: ${registrationData.email}, ${registrationData.fullName}, " +
                                "${registrationData.birthDate}, ${registrationData.identityType}, $password"
                            )
                        }

                        // Trigger registration
                        viewModel.register()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF4A90E2) else Color(0xFFD9D9D9),
                    contentColor = if (isFormValid) Color.White else Color(0xFF8C8C8C)
                ),
                enabled = isFormValid
            ) {
                if (registerState is Resource.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Daftar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Debug information
            Text(
                text = "Current Data: Email=${registrationData.email}, " +
                      "Name=${registrationData.fullName}, " +
                      "Birth=${registrationData.birthDate}, " +
                      "Identity=${registrationData.identityType}",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Snackbar for messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(
                id = if (isMet) R.drawable.check_circle else R.drawable.check_circle_outline
            ),
            contentDescription = null,
            tint = if (isMet) Color(0xFF79E6C0) else Color(0xFF999999),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (isMet) Color(0xFF79E6C0) else Color(0xFF999999),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
} 