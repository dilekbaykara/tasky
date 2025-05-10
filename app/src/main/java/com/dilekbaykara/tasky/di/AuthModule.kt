package com.dilekbaykara.tasky.di

import android.util.Log
import com.dilekbaykara.tasky.BuildConfig
import com.dilekbaykara.tasky.domain.repository.AuthRepositoryImpl
import com.dilekbaykara.tasky.data.remote.TaskyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    @Singleton
    fun provideRetrofit(@Named("apiKey") apiKey: String): Retrofit {
        Log.e("APIKEY", apiKey)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", apiKey)
                    .build()
                chain.proceed(request)
            }
            .build()


        return Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com/")
            .client(client)
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
    fun provideAuthRepository(api: TaskyApi): AuthRepositoryImpl {
        return AuthRepositoryImpl(api)
    }


    @Provides
    @Named("apiKey")
    fun provideApiKey(): String = BuildConfig.TASKY_API_KEY


//    @Provides
//    @Named("apiKey")
//    fun provideApiKey(): String = " iGi0PdJTjRnfhdqv"
}