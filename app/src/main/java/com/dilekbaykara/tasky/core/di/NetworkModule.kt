package com.dilekbaykara.tasky.core.di
import android.content.Context
import com.dilekbaykara.tasky.BuildConfig
import com.dilekbaykara.tasky.features.auth.data.AuthRepositoryImpl
import com.dilekbaykara.tasky.features.auth.data.AuthService
import com.dilekbaykara.tasky.features.auth.data.TokenAuthenticator
import com.dilekbaykara.tasky.features.shared.data.network.NetworkConnectivityManager
import com.dilekbaykara.tasky.features.shared.data.remote.TaskyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @Named("apiKey")
    fun provideApiKeyInterceptor(@Named("apiKey") apiKey: String): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("x-api-key", apiKey)
                .build()
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    @Named("accessToken")
    fun provideAccessTokenInterceptor(authService: AuthService): Interceptor {
        return Interceptor { chain ->
            val accessToken = authService.getToken()
            val request = if (!accessToken.isNullOrEmpty()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshOkHttpClient(@Named("apiKey") apiKeyInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshRetrofit(@Named("refresh") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshTaskyApi(@Named("refresh") retrofit: Retrofit): TaskyApi {
        return retrofit.create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("apiKey") apiKeyInterceptor: Interceptor,
        @Named("accessToken") accessTokenInterceptor: Interceptor,
        authService: AuthService,
        @Named("refresh") refreshApi: TaskyApi
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(accessTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(authService, refreshApi))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskyApi(retrofit: Retrofit): TaskyApi {
        return retrofit.create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityManager(
        @ApplicationContext context: Context
    ): NetworkConnectivityManager {
        return NetworkConnectivityManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        api: TaskyApi,
        networkManager: NetworkConnectivityManager
    ): AuthRepositoryImpl {
        return AuthRepositoryImpl(authService, api, networkManager)
    }

    @Provides
    @Named("apiKey")
    fun provideApiKey(): String {
        return BuildConfig.TASKY_API_KEY
    }
}
