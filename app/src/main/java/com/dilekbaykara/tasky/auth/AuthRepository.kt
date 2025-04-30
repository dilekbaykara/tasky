package com.dilekbaykara.tasky.auth

import com.dilekbaykara.tasky.auth.api.TaskyApi
import com.dilekbaykara.tasky.auth.models.AccessTokenResponse
import com.dilekbaykara.tasky.auth.models.LoginRequest
import com.dilekbaykara.tasky.auth.models.LoginResponse
import com.dilekbaykara.tasky.auth.models.RefreshTokenRequest
import com.dilekbaykara.tasky.auth.models.RegisterRequest
import dagger.Provides
import javax.inject.Inject

class AuthRepository @Inject constructor(private val api: TaskyApi) {

    suspend fun register(request: RegisterRequest): Result<Unit> = try {
        val res = api.register(request)
        if (res.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(res.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }

    @Provides
    suspend fun refreshToken(request: RefreshTokenRequest): Result<AccessTokenResponse> = try {

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun isTokenValid(accessToken: String): Boolean = try {

    } catch (_: Exception) {
        false
    }

    suspend fun logout(accessToken: String): Result<Unit> = try {

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(request: LoginRequest): Result<LoginResponse> = try {

    } catch (e: Exception) {
        Result.failure(e)
    }

}