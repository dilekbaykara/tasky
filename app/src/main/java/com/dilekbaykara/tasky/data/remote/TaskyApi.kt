package com.dilekbaykara.tasky.data.remote

import com.dilekbaykara.tasky.domain.model.AccessTokenResponse
import com.dilekbaykara.tasky.domain.model.LoginRequest
import com.dilekbaykara.tasky.domain.model.LoginResponse
import com.dilekbaykara.tasky.domain.model.RefreshTokenRequest
import com.dilekbaykara.tasky.domain.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskyApi {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("accessToken")
    suspend fun refreshAccessToken(@Body request: RefreshTokenRequest): Response<AccessTokenResponse>

    @GET("authenticate")
    suspend fun authenticate(@Header("Authorization") auth: String): Response<Unit>

    @GET("logout")
    suspend fun logout(@Header("Authorization") auth: String): Response<Unit>


}