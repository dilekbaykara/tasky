package com.dilekbaykara.tasky.auth.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.auth.AuthRepository
import com.dilekbaykara.tasky.auth.models.LoginRequest
import com.dilekbaykara.tasky.auth.models.LoginResponse
import com.dilekbaykara.tasky.auth.models.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginState: StateFlow<Result<LoginResponse>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Result<Unit>?>(null)
    val registerState: StateFlow<Result<Unit>?> = _registerState.asStateFlow()

    private val _error = MutableStateFlow<Error>(Error.None)
    val error: StateFlow<Error> = _error.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if(!validateEmail(email)) {
                _error.value = Error.Validation("Invalid Email")
                return@launch
            }
            _loginState.value = repository.login(LoginRequest(email, password))
        }
    }

    fun registration(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = repository.register(RegisterRequest(fullName, email, password))
        }
    }

    fun hideErrorDialog() {
        _error.value = Error.None
    }
}

fun validateEmail(email: String) : Boolean {
    val atIndex = email.indexOf("@")
    val lastDotIndex = email.lastIndexOf(".")
    return atIndex > 0 && lastDotIndex > atIndex && lastDotIndex < email.length
}

sealed class Error {
    data object None : Error()
    data class Validation(val message: String) : Error()
    data class ApiError(val message: String) : Error()
}