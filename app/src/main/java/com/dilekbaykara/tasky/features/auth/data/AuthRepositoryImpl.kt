package com.dilekbaykara.tasky.features.auth.data
import com.dilekbaykara.tasky.features.auth.domain.AccessTokenResponse
import com.dilekbaykara.tasky.features.auth.domain.LoginRequest
import com.dilekbaykara.tasky.features.auth.domain.LoginResponse
import com.dilekbaykara.tasky.features.auth.domain.RefreshTokenRequest
import com.dilekbaykara.tasky.features.auth.domain.RegisterRequest
import com.dilekbaykara.tasky.features.shared.data.network.NetworkConnectivityManager
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val api: TaskyApi,
    private val networkManager: NetworkConnectivityManager
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            if (!networkManager.isOnline()) {
                return Result.failure(IOException("No internet connection. Please check your network and try again."))
            }
            val response = api.register(request)
            if (response.isSuccessful) {
                val loginResult = login(LoginRequest(request.email, request.password))
                if (loginResult.isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.success(Unit)
                }
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "Email already exists. Please use a different email."
                    400 -> "Invalid registration data. Please check your input."
                    else -> "Registration failed. Please try again."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                409 -> "Email already exists. Please use a different email."
                400 -> "Invalid registration data. Please check your input."
                500 -> "Server error. Please try again later."
                else -> "Registration failed. Please try again."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your connection and try again."))
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed. Please try again."))
        }
    }
    override suspend fun refreshToken(request: RefreshTokenRequest): Result<AccessTokenResponse> {
        return Result.success(AccessTokenResponse("mock_token", 3600))
    }
    override suspend fun isTokenValid(accessToken: String): Boolean {
        return authService.isAuthenticated()
    }
    override suspend fun logout(accessToken: String): Result<Unit> {
        return try {
            if (accessToken.isBlank()) {
                return Result.success(Unit)
            }
            if (networkManager.isOnline()) {
                try {
                    val response = api.logout()
                } catch (e: Exception) {
                }
            }
            authService.clearUserSession()
            Result.success(Unit)
        } catch (e: Exception) {
            authService.clearUserSession()
            Result.success(Unit)
        }
    }
    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            if (!networkManager.isOnline()) {
                return Result.failure(IOException("No internet connection. Please check your network and try again."))
            }
            val response = api.login(request)
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    authService.saveUserSession(
                        loginResponse.AccessToken,
                        loginResponse.RefreshToken,
                        loginResponse.UserId,
                        loginResponse.FullName
                    )
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Login failed. Please try again."))
                }
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid email or password. Please check your credentials."
                    404 -> "Account not found. Please check your email or register first."
                    400 -> "Invalid login data. Please check your input."
                    else -> "Login failed. Please try again."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Invalid email or password. Please check your credentials."
                404 -> "Account not found. Please check your email or register first."
                400 -> "Invalid login data. Please check your input."
                500 -> "Server error. Please try again later."
                else -> "Login failed. Please try again."
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your connection and try again."))
        } catch (e: Exception) {
            Result.failure(Exception("Login failed. Please try again."))
        }
    }
}