package com.bonustrack02.tp08goodprice

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object {
        fun getInstance() : Retrofit {
            val builder = Retrofit.Builder()
            builder.baseUrl("http://openapi.seoul.go.kr:8088")
            builder.addConverterFactory(ScalarsConverterFactory.create())
//            builder.addConverterFactory(GsonConverterFactory.create())
            builder.addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory(MediaType.parse("application/json")!!))

            return builder.build()
        }
    }
}