package com.dilekbaykara.tasky.features.auth.presentation.register
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilekbaykara.tasky.features.auth.data.AuthRepositoryImpl
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.auth.domain.LoginRequest
import com.dilekbaykara.tasky.features.auth.domain.LoginResponse
import com.dilekbaykara.tasky.features.auth.domain.RegisterRequest
import com.dilekbaykara.tasky.features.shared.data.network.NetworkConnectivityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl,
    private val authService: AuthService,
    private val networkManager: NetworkConnectivityManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginState: StateFlow<Result<LoginResponse>?> = _loginState.asStateFlow()
    private val _registerState = MutableStateFlow<Result<Unit>?>(null)
    val registerState: StateFlow<Result<Unit>?> = _registerState.asStateFlow()
    private val _error = MutableStateFlow<Error?>(null)
    val error: StateFlow<Error?> = _error.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _requiresNetwork = MutableStateFlow(false)
    val requiresNetwork: StateFlow<Boolean> = _requiresNetwork.asStateFlow()
    val isLoggedIn: StateFlow<Boolean> = authService.isLoggedIn as StateFlow<Boolean>
    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (!networkManager.isOnline() && !authService.isAuthenticated()) {
                _requiresNetwork.value = true
                return@launch
            }
            if (!validateEmail(email)) {
                _error.value = Error.Validation("Invalid Email")
                return@launch
            }
            _isLoading.value = true
            _error.value = null
            val result = repository.login(LoginRequest(email, password))
            _loginState.value = result
            if (result.isFailure) {
                _error.value = Error.ApiError(result.exceptionOrNull()?.message ?: "Login failed")
            }
            _isLoading.value = false
        }
    }
    fun registration(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            if (!networkManager.isOnline()) {
                _requiresNetwork.value = true
                return@launch
            }
            if (!validateEmail(email)) {
                _error.value = Error.Validation("Invalid Email")
                return@launch
            }
            if (fullName.isBlank()) {
                _error.value = Error.Validation("Full name is required")
                return@launch
            }
            if (password.length < 6) {
                _error.value = Error.Validation("Password must be at least 6 characters")
                return@launch
            }
            _isLoading.value = true
            _error.value = null
            val result = repository.register(RegisterRequest(fullName, email, password))
            _registerState.value = result
            if (result.isFailure) {
                _error.value = Error.ApiError(result.exceptionOrNull()?.message ?: "Registration failed")
            }
            _isLoading.value = false
        }
    }
    fun logout() {
        viewModelScope.launch {
            val currentToken = authService.getToken() ?: ""
            repository.logout(currentToken)
            _loginState.value = null
            _registerState.value = null
            _error.value = null
        }
    }
    fun clearAuthStates() {
        _loginState.value = null
        _registerState.value = null
        _error.value = null
    }
    fun isUserLoggedIn(): Boolean {
        return authService.isAuthenticated()
    }
    fun getCurrentUserName(): String? {
        return authService.getFullName()
    }
    fun hideErrorDialog() {
        _error.value = null
    }
    fun hideNetworkDialog() {
        _requiresNetwork.value = false
    }
    fun checkNetworkAndRetry() {
        if (networkManager.isOnline()) {
            _requiresNetwork.value = false
        }
    }
}
fun validateEmail(email: String): Boolean {
    val atIndex = email.indexOf("@")
    val lastDotIndex = email.lastIndexOf(".")
    return atIndex > 0 && lastDotIndex > atIndex && lastDotIndex < email.length
}
sealed class Error {
    data object None : Error()
    data class Validation(val message: String) : Error()
    data class ApiError(val message: String) : Error()
}
