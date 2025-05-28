package com.dilekbaykara.tasky.domain.repository


import android.util.Log
import com.dilekbaykara.tasky.data.remote.TaskyApi
import com.dilekbaykara.tasky.data.repository.AuthRepository
import com.dilekbaykara.tasky.domain.model.AccessTokenResponse
import com.dilekbaykara.tasky.domain.model.LoginRequest
import com.dilekbaykara.tasky.domain.model.LoginResponse
import com.dilekbaykara.tasky.domain.model.RefreshTokenRequest
import com.dilekbaykara.tasky.domain.model.RegisterRequest
import dagger.Provides
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(private val api: TaskyApi): AuthRepository {

    override suspend fun register(request: RegisterRequest): Result<Unit> = try {
        Log.e("REQUEST ", "$request")
        val res = api.register(request)
        if (res.isSuccessful) {
            Result.success(Unit)
        } else {
            val error = res.message()
            Log.e("FAILED", error.toString())
            Log.e("error code", res.code().toString())
            Log.e("error headers", res.headers().toString())
            Result.failure(Exception(res.errorBody()?.string()))
        }
    } catch (e: Exception) {
        Log.e("FAILED", e.message.toString())
        Result.failure(e)
    }

    @Provides
    override suspend fun refreshToken(request: RefreshTokenRequest): Result<AccessTokenResponse> = try {
        val response = api.refreshAccessToken(request)
        if(response.isSuccessful) {
            val body = response.body()
            if(body != null) Result.success(body) else Result.failure(Exception("Nothing"))
        } else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun isTokenValid(accessToken: String): Boolean = try {
        val response = api.authenticate("Bearer $accessToken")
        response.isSuccessful
    } catch (_: Exception) {
        false
    }

    override suspend fun logout(accessToken: String): Result<Unit> = try {
        val response = api.authenticate("Bearer $accessToken")
        if(response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponse> = try {
        val response = api.login(request)
        if(response.isSuccessful) {
            val body = response.body()
            if(body != null) Result.success(body) else Result.failure(Exception("Nothing"))
        } else Result.failure(Exception(response.errorBody()?.string()))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
