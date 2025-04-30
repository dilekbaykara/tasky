package com.dilekbaykara.tasky.auth.models

class AuthModels {
}

data class RegisterRequest (
    val FullName: String,
    val Email: String,
    val Password: String
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