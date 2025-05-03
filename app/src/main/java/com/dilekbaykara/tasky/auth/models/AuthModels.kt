package com.dilekbaykara.tasky.auth.models


import com.google.gson.annotations.SerializedName




data class RegisterRequest (
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)


data class LoginRequest (
    val Email: String,
    val Password: String
)


data class LoginResponse (
    val AccessToken: String,
    val RefreshToken: String,
    val FullName: String
)


data class RefreshTokenRequest (
    val RefreshToken: String,
    val UserId: String
)


data class AccessTokenResponse (
    val AccessToken: String,
    val ExpirationTimeStamp: Long


)
