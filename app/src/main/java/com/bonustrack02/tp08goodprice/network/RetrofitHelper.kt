package com.bonustrack02.tp08goodprice.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object {
        fun getInstance(baseUrl: String) : Retrofit {
            val builder = Retrofit.Builder()
            builder.apply {
                baseUrl(baseUrl)
                addConverterFactory(ScalarsConverterFactory.create())
                addConverterFactory(Json {ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaTypeOrNull()!!))
            }
            return builder.build()
        }
    }
}