package com.dilekbaykara.tasky.features.auth.data

import com.dilekbaykara.tasky.features.auth.domain.RefreshTokenRequest
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named

class TokenAuthenticator @Inject constructor(
    private val authService: AuthService,
    @Named("refresh") private val refreshApi: TaskyApi
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = authService.getRefreshToken() ?: return null
        val userId = authService.getUserId() ?: return null
        val refreshRequest = RefreshTokenRequest(refreshToken, userId)
        val refreshResponse = runBlocking {
            refreshApi.refreshAccessToken(refreshRequest)
        }
        if (refreshResponse.isSuccessful) {
            val newAccessToken = refreshResponse.body()?.AccessToken
            if (!newAccessToken.isNullOrEmpty()) {
                authService.saveUserSession(newAccessToken, refreshToken, userId, authService.getFullName())
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }
        }
        // If refresh fails, logout user or return null
        authService.clearUserSession()
        return null
    }
}