package com.dilekbaykara.tasky.features.auth.data
import com.dilekbaykara.tasky.features.auth.domain.AccessTokenResponse
import com.dilekbaykara.tasky.features.auth.domain.LoginRequest
import com.dilekbaykara.tasky.features.auth.domain.LoginResponse
import com.dilekbaykara.tasky.features.auth.domain.RefreshTokenRequest
import com.dilekbaykara.tasky.features.auth.domain.RegisterRequest
interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<Unit>
    suspend fun refreshToken(request: RefreshTokenRequest): Result<AccessTokenResponse>
    suspend fun isTokenValid(accessToken: String): Boolean
    suspend fun logout(accessToken: String): Result<Unit>
    suspend fun login(request: LoginRequest): Result<LoginResponse>
}