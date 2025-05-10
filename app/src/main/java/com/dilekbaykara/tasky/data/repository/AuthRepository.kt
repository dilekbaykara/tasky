package com.dilekbaykara.tasky.data.repository

import com.dilekbaykara.tasky.domain.model.AccessTokenResponse
import com.dilekbaykara.tasky.domain.model.LoginRequest
import com.dilekbaykara.tasky.domain.model.LoginResponse
import com.dilekbaykara.tasky.domain.model.RefreshTokenRequest
import com.dilekbaykara.tasky.domain.model.RegisterRequest

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<Unit>

    suspend fun refreshToken(request: RefreshTokenRequest): Result<AccessTokenResponse>
    suspend fun isTokenValid(accessToken: String): Boolean

    suspend fun logout(accessToken: String): Result<Unit>
    suspend fun login(request: LoginRequest): Result<LoginResponse>
}