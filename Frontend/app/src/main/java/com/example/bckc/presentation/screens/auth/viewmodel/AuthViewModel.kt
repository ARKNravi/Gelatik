package com.example.bckc.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.domain.usecase.auth.LoginUseCase
import com.example.bckc.domain.usecase.auth.RegisterUseCase
import com.example.bckc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Boolean>?>(null)
    val loginState: StateFlow<Resource<Boolean>?> = _loginState

    private val _registerState = MutableStateFlow<Resource<Boolean>?>(null)
    val registerState: StateFlow<Resource<Boolean>?> = _registerState

    // Registration form state
    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData: StateFlow<RegistrationData> = _registrationData

    fun updateRegistrationData(
        email: String? = null,
        fullName: String? = null,
        birthDate: String? = null,
        identityType: String? = null,
        password: String? = null,
        passwordConfirm: String? = null
    ) {
        _registrationData.value = _registrationData.value.copy(
            email = email ?: _registrationData.value.email,
            fullName = fullName ?: _registrationData.value.fullName,
            birthDate = birthDate ?: _registrationData.value.birthDate,
            identityType = identityType ?: _registrationData.value.identityType,
            password = password ?: _registrationData.value.password,
            passwordConfirm = passwordConfirm ?: _registrationData.value.passwordConfirm
        )
    }

    fun register() {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val data = _registrationData.value
            _registerState.value = registerUseCase(
                email = data.email,
                fullName = data.fullName,
                birthDate = data.birthDate,
                identityType = data.identityType,
                password = data.password,
                passwordConfirm = data.passwordConfirm
            )
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
}

data class RegistrationData(
    val email: String = "",
    val fullName: String = "",
    val birthDate: String = "",
    val identityType: String = "",
    val password: String = "",
    val passwordConfirm: String = ""
)
