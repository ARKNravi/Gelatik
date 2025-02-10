package com.example.bckc.presentation.screens.profile

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.activity.compose.BackHandler
import coil.compose.AsyncImage
import com.example.bckc.R
import com.example.bckc.presentation.screens.profile.viewmodel.EditProfileViewModel
import com.example.bckc.presentation.screens.profile.viewmodel.IdentityType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Date Picker setup
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }.time
            viewModel.updateBirthDate(selectedDate)
        },
        year, month, day
    )

    // Show success dialog if update is successful
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            }
        )
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            onDismiss = { showConfirmationDialog = false },
            onConfirm = onNavigateBack
        )
    }

    val handleBackPress = {
        println("Back pressed. Has unsaved changes: ${uiState.hasUnsavedChanges}")
        if (uiState.hasUnsavedChanges) {
            showConfirmationDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Handle system back button
    BackHandler {
        handleBackPress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profil",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1B1D28)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBackPress,
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(128.dp)
            ) {
                AsyncImage(
                    model = uiState.profilePictureUrl ?: "https://hebbkx1anhila5yf.public.blob.vercel-storage.com/STUDEAF_DESIGN__Copy-zOrlmwsOjlaa95s7ABRDnpvGVdbuQT.png",
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF2171CF), CircleShape),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { /* Handle edit photo */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color(0xFF2171CF), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit photo",
                        tint = Color.White
                    )
                }
            }

            // Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Full Name
                FormField(
                    label = "Nama Lengkap",
                    value = uiState.fullName,
                    onValueChange = viewModel::updateFullName
                )

                // Birth Date
                Column {
                    Text(
                        text = "Tanggal Lahir",
                        color = Color(0xFF838383),
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Select date",
                                tint = Color(0xFF838383),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = uiState.birthDate?.let { dateFormatter.format(it) } ?: "",
                            onValueChange = { },
                            enabled = false,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                disabledBorderColor = Color(0xFFE2E8F0),
                                disabledTextColor = Color(0xFF1B1D28)
                            )
                        )
                    }
                }

                // Email
                FormField(
                    label = "Email",
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    enabled = false
                )

                // Identity Type
                Column {
                    Text(
                        text = "Pilihan Identitas",
                        color = Color(0xFF838383),
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IdentityButton(
                            text = "Tuli",
                            selected = uiState.identityType == IdentityType.TULI,
                            onClick = { viewModel.updateIdentityType(IdentityType.TULI) },
                            modifier = Modifier.weight(1f)
                        )
                        IdentityButton(
                            text = "Dengar",
                            selected = uiState.identityType == IdentityType.DENGAR,
                            onClick = { viewModel.updateIdentityType(IdentityType.DENGAR) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Institution
                FormField(
                    label = "Institusi / Lembaga",
                    value = uiState.institution,
                    onValueChange = viewModel::updateInstitution,
                    placeholder = "Masukkan institusi / lembaga"
                )

                // Save Button
                Button(
                    onClick = { viewModel.saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    enabled = uiState.isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isFormValid) Color(0xFF2171CF) else Color(0xFFF5F5F5),
                        contentColor = if (uiState.isFormValid) Color.White else Color(0xFF838383)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF838383),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let {
                { Text(text = it, color = Color(0xFF838383)) }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF2171CF),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = enabled
        )
    }
}

@Composable
fun IdentityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) Color(0xFF2171CF) else Color(0xFFE2E8F0)
        )
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF2171CF) else Color(0xFF1B1D28)
        )
    }
}

@Composable
fun SuccessDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Text(
                    text = "Data Berhasil Diperbarui!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B1D28)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2171CF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "OK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Apakah Anda yakin ingin membatalkan perubahan?",
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 32.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1B1D28)
                )

                Divider(
                    color = Color(0xFFE2E8F0),
                    thickness = 1.dp
                )

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    // No Button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Tidak",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFDC2626) // Red color
                        )
                    }

                    // Vertical Divider
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                        color = Color(0xFFE2E8F0)
                    )

                    // Yes Button
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Ya",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2171CF) // Blue color
                        )
                    }
                }
            }
        }
    }
}
