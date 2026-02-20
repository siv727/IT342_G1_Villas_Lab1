package com.citu.ura.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient – singleton HTTP client, equivalent to axiosClient.js in the web frontend.
 *
 * - BASE_URL uses 10.0.2.2 which maps to the host machine's localhost from the Android Emulator.
 *   Change to your PC's local IP if using a physical device.
 * - Interceptors attach the Bearer token to every request (like axios.interceptors.request).
 * - Logging interceptor logs request/response bodies for debugging.
 */
object RetrofitClient {

    // 10.0.2.2 = host machine localhost from Android Emulator
    // For physical device, use your PC's IP (e.g., "http://192.168.1.100:8080/")
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            // Attach Bearer token to every request (equivalent to axios request interceptor)
            val token = TokenManager.getToken()
            val requestBuilder = chain.request().newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            requestBuilder.addHeader("Content-Type", "application/json")
            chain.proceed(requestBuilder.build())
        }
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}