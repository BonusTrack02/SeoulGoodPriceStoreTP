package com.bonustrack02.tp08goodprice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {
    @GET("/{key}/json/ListPriceModelStoreService/{startIndex}/{endIndex}/{inDutyCode}")
    fun getStoreJson(@Path("key") key: String, @Path("startIndex") startIndex: Int, @Path("endIndex") endIndex: Int, @Path("inDutyCode") inDutyCode: String): Call<RetrofitResponse>
}