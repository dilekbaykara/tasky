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
        val response = api.refreshAccessToken(request)
        if(response.isSuccessful) {
            val body = response.body()
            if(body != null) Result.success(body) else Result.failure(Exception("Nothing"))
        } else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }


    suspend fun isTokenValid(accessToken: String): Boolean = try {
        val response = api.authenticate("Bearer $accessToken")
        response.isSuccessful
    } catch (_: Exception) {
        false
    }


    suspend fun logout(accessToken: String): Result<Unit> = try {
        val response = api.authenticate("Bearer $accessToken")
        if(response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }


    suspend fun login(request: LoginRequest): Result<LoginResponse> = try {
        val response = api.login(request)
        if(response.isSuccessful) {
            val body = response.body()
            if(body != null) Result.success(body) else Result.failure(Exception("Nothing"))
        } else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
