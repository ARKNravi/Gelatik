package com.example.bckc.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bckc.R
import com.example.bckc.presentation.screens.profile.viewmodel.SecurityEvent
import com.example.bckc.presentation.screens.profile.viewmodel.SecurityUiEvent
import com.example.bckc.presentation.screens.profile.viewmodel.SecurityViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }

    val requirements = listOf(
        "Minimal 8 karakter" to { s: String -> s.length >= 8 },
        "Karakter huruf kecil" to { s: String -> s.any { it.isLowerCase() } },
        "Karakter angka" to { s: String -> s.any { it.isDigit() } },
        "Karakter unik (!@#\$)" to { s: String -> s.any { "!@#$".contains(it) } },
        "Karakter huruf besar" to { s: String -> s.any { it.isUpperCase() } }
    )

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SecurityUiEvent.ShowError -> showError = event.message
                is SecurityUiEvent.NavigateBack -> onBackClick()
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFE8F1FF),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    tint = Color(0xFF1B1D28)
                )
            }

            Text(
                text = "Keamanan",
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1D28)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text(
                text = "Ubah Kata Sandi",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B1D28)
            )

            Text(
                text = "Masukkan kata sandi baru Anda",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp,
                color = Color(0xFF8C8C8C)
            )

            // Password Fields
            PasswordField(
                label = "Kata Sandi",
                value = password,
                onValueChange = { 
                    password = it
                    showError = null
                },
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = it },
                isError = showError != null,
                errorMessage = showError,
                modifier = Modifier.padding(top = 24.dp)
            )

            // Password Requirements
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                requirements.forEach { (text, validator) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (validator(password)) Color(0xFF00D589) else Color(0xFFE2E8F0),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            color = if (validator(password)) Color(0xFF00D589) else Color(0xFF8C8C8C)
                        )
                    }
                }
            }

            PasswordField(
                label = "Konfirmasi Kata Sandi",
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    showError = null
                },
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = it },
                isError = showError != null,
                errorMessage = showError
            )

            // Save Button
            Button(
                onClick = { viewModel.onEvent(SecurityEvent.ChangePassword(password, confirmPassword)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2171CF)
                ),
                enabled = password.isNotEmpty() && confirmPassword.isNotEmpty() && !viewModel.state.isLoading
            ) {
                if (viewModel.state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Simpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1B1D28)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            visualTransformation = if (passwordVisible) 
                VisualTransformation.None 
            else 
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { onVisibilityChange(!passwordVisible) }
                ) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.eyeopen
                            else R.drawable.eyeclose
                        ),
                        contentDescription = if (passwordVisible) 
                            "Hide password" 
                        else 
                            "Show password",
                        tint = Color(0xFF8C8C8C)
                    )
                }
            },
            isError = isError,
            supportingText = errorMessage?.let { 
                { Text(text = it, color = MaterialTheme.colorScheme.error) }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2171CF),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
    }
}
