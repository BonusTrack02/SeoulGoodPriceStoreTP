package com.bonustrack02.tp08goodprice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitResponse(
    @SerialName("ListPriceModelStoreService") val responseItem: ResponseItem
)

@Serializable
data class ResponseItem(
    @SerialName("list_total_count") val totalCount: Int,
    @SerialName("RESULT") val result: Result,
    @SerialName("row") val ShopList: MutableList<Shop>
)

@Serializable
data class Result(
    @SerialName("CODE") val code: String,
    @SerialName("MESSAGE")val message: String
)

@Serializable
data class Shop(
    @SerialName("SH_ID") var shopId: String = "", // 업소 아이디(고유 번호)
    @SerialName("SH_PHOTO") var shopImage: String = "", // 업소 사진
    @SerialName("SH_NAME") var shopName: String = "", // 업소 이름
    @SerialName("SH_ADDR") var shopAddress: String = "", // 업소 주소
    @SerialName("SH_PHONE") var shopNumber: String= "", // 업소 전화번호
    @SerialName("SH_PRIDE") var shopPride: String = "", // 자랑거리
)
