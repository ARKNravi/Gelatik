package com.example.bckc.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.domain.usecase.auth.LoginUseCase
import com.example.bckc.domain.usecase.auth.RegisterUseCase
import com.example.bckc.utils.Resource
import com.example.bckc.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Boolean>?>(null)
    val loginState: StateFlow<Resource<Boolean>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<Boolean>?>(null)
    val registerState: StateFlow<Resource<Boolean>?> = _registerState

    // Registration form state
    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData: StateFlow<RegistrationData> = _registrationData

    init {
        // Load saved registration data when ViewModel is created
        val savedData = preferenceManager.getRegistrationData()
        _registrationData.value = RegistrationData(
            email = savedData["email"] ?: "",
            fullName = savedData["fullName"] ?: "",
            birthDate = savedData["birthDate"] ?: "",
            identityType = savedData["identityType"] ?: ""
        )
    }

    fun updateRegistrationData(
        email: String? = null,
        fullName: String? = null,
        birthDate: String? = null,
        identityType: String? = null,
        password: String? = null,
        passwordConfirm: String? = null
    ) {
        val currentData = _registrationData.value
        val updatedData = currentData.copy(
            email = email ?: currentData.email,
            fullName = fullName ?: currentData.fullName,
            birthDate = birthDate ?: currentData.birthDate,
            identityType = identityType ?: currentData.identityType,
            password = password ?: currentData.password,
            passwordConfirm = passwordConfirm ?: currentData.passwordConfirm
        )
        _registrationData.value = updatedData
        
        // Save to SharedPreferences if basic data is updated
        if (email != null || fullName != null || birthDate != null || identityType != null) {
            preferenceManager.saveRegistrationData(
                email = updatedData.email,
                fullName = updatedData.fullName,
                birthDate = updatedData.birthDate,
                identityType = updatedData.identityType
            )
        }
        
        // Debug log to verify data update
        println("Previous data: $currentData")
        println("Updated registration data: $updatedData")
    }

    fun register() {
        viewModelScope.launch {
            val data = _registrationData.value
            
            // Debug log
            println("Attempting registration with data: $data")
            
            // Load saved data from preferences to ensure we have the latest
            val savedData = preferenceManager.getRegistrationData()
            val registrationData = data.copy(
                email = savedData["email"] ?: data.email,
                fullName = savedData["fullName"] ?: data.fullName,
                birthDate = savedData["birthDate"] ?: data.birthDate,
                identityType = savedData["identityType"] ?: data.identityType
            )
            
            // Validate data before sending
            if (registrationData.email.isBlank() || 
                registrationData.fullName.isBlank() || 
                registrationData.birthDate.isBlank() || 
                registrationData.identityType.isBlank() || 
                registrationData.password.isBlank() || 
                registrationData.passwordConfirm.isBlank()
            ) {
                println("Empty fields found in: $registrationData")
                _registerState.value = Resource.Error("All fields must be filled")
                return@launch
            }

            // Format birth date from DD/MM/YYYY to YYYY-MM-DD
            val parts = registrationData.birthDate.split("/")
            val formattedDate = if (parts.size == 3) {
                "${parts[2]}-${parts[1]}-${parts[0]}"
            } else {
                registrationData.birthDate
            }

            println("Sending to API: email=${registrationData.email}, name=${registrationData.fullName}, birth=$formattedDate, type=${registrationData.identityType}, pass=${registrationData.password}")

            _registerState.value = Resource.Loading()
            _registerState.value = registerUseCase(
                email = registrationData.email,
                fullName = registrationData.fullName,
                birthDate = formattedDate,
                identityType = registrationData.identityType,
                password = registrationData.password,
                passwordConfirm = registrationData.passwordConfirm
            )

            // Clear registration data after successful registration
            if (_registerState.value is Resource.Success) {
                preferenceManager.clearRegistrationData()
                clearRegistrationData()
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            _loginState.value = loginUseCase(email, password)
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }

    fun resetRegisterState() {
        _registerState.value = null
    }

    fun clearRegistrationData() {
        _registrationData.value = RegistrationData()
        preferenceManager.clearRegistrationData()
    }
}

data class RegistrationData(
    val email: String = "",
    val fullName: String = "",
    val birthDate: String = "",
    val identityType: String = "",
    val password: String = "",
    val passwordConfirm: String = ""
)
