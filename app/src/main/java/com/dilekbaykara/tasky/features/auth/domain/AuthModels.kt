package com.dilekbaykara.tasky.features.auth.domain
import com.google.gson.annotations.SerializedName
data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
data class LoginResponse(
    @SerializedName("accessToken") val AccessToken: String,
    @SerializedName("refreshToken") val RefreshToken: String,
    @SerializedName("userId") val UserId: String,
    @SerializedName("fullName") val FullName: String,
    @SerializedName("accessTokenExpirationTimestamp") val ExpirationTimestamp: Long
)
data class RefreshTokenRequest(
    val RefreshToken: String,
    val UserId: String
)
data class AccessTokenResponse(
    val AccessToken: String,
    val ExpirationTimeStamp: Long
)