package com.bonustrack02.tp08goodprice.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun getInstance(baseUrl: String) : Retrofit {
            val clientBuilder = OkHttpClient.Builder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(interceptor)
            val builder = Retrofit.Builder().apply {
                baseUrl(baseUrl)
                addConverterFactory(ScalarsConverterFactory.create())
                addConverterFactory(json.asConverterFactory("application/json".toMediaTypeOrNull()!!))
                client(clientBuilder.build())
            }
            return builder.build()
        }
    }
}