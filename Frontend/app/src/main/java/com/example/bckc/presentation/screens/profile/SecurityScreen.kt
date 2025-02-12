package com.example.bckc.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SecurityScreen(
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SecurityUiEvent.NavigateToChangePassword -> onContinueClick()
                is SecurityUiEvent.ShowError -> showError = event.message
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
                text = "Masukkan kata sandi lama Anda untuk melanjutkan",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp,
                color = Color(0xFF8C8C8C)
            )

            // Password Field
            Column(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = "Kata Sandi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B1D28)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        showError = null 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    visualTransformation = if (passwordVisible) 
                        VisualTransformation.None 
                    else 
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
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
                    isError = showError != null,
                    supportingText = showError?.let { 
                        { Text(text = it, color = MaterialTheme.colorScheme.error) }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2171CF),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                // Forgot Password Link
                TextButton(
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Lupa Kata Sandi?",
                        color = Color(0xFF2171CF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Continue Button
            Button(
                onClick = { viewModel.onEvent(SecurityEvent.VerifyPassword(password)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2171CF)
                ),
                enabled = password.isNotEmpty() && !viewModel.state.isLoading
            ) {
                if (viewModel.state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
