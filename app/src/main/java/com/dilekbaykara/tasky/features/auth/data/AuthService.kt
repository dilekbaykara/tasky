package com.dilekbaykara.tasky.features.auth.data
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AuthService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()
    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USER_ID_KEY = "user_id"
        private const val FULL_NAME_KEY = "full_name"
    }
    init {
        refreshAuthState()
    }
    fun refreshAuthState() {
        val hasToken = sharedPreferences.getString(TOKEN_KEY, null) != null
        _isLoggedIn.value = hasToken
    }
    fun saveUserSession(token: String?, refreshToken: String?, userId: String?, fullName: String?) {
        if (token.isNullOrEmpty()) {
            return
        }
        if (userId.isNullOrEmpty()) {
            return
        }
        val safeFullName = fullName ?: ""
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.putString(REFRESH_TOKEN_KEY, refreshToken)
        editor.putString(USER_ID_KEY, userId)
        editor.putString(FULL_NAME_KEY, safeFullName)
        val success = editor.commit()
        if (success) {
            _isLoggedIn.value = true
        }
    }
    fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.remove(REFRESH_TOKEN_KEY)
        editor.remove(USER_ID_KEY)
        editor.remove(FULL_NAME_KEY)
        val success = editor.commit()
        if (success) {
            _isLoggedIn.value = false
        }
    }
    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }
    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }
    fun getFullName(): String? {
        return sharedPreferences.getString(FULL_NAME_KEY, null)
    }
    fun isAuthenticated(): Boolean {
        val token = getToken()
        return !token.isNullOrEmpty()
    }
    fun getCurrentUserId(): String {
        return getUserId() ?: "default_user"
    }
}