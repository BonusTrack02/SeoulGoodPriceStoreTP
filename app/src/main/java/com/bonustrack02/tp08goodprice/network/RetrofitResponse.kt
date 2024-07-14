package com.bonustrack02.tp08goodprice.network

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
    @SerialName("row") val shopList: MutableList<Shop>
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

@Serializable
data class ResponseKeyword(
    @SerialName("meta") val meta: Meta,
    @SerialName("documents") val placeList: MutableList<Place>
)

@Serializable
data class Meta(
    @SerialName("is_end") val isEnd: Boolean,
    @SerialName("pageable_count") val pageCount: Int,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("same_name") val sameName: SameName,
)

@Serializable
data class SameName(
    @SerialName("keyword") val keyword: String,
    @SerialName("selected_region") val selectedRegion: String,
)

@Serializable
data class Place(
    @SerialName("address_name") val address: String,
    @SerialName("category_group_name") val categoryName: String,
    @SerialName("category_group_code") val categoryCode: String,
    @SerialName("phone") val phone: String,
    @SerialName("place_name") val placeName: String,
    @SerialName("place_url") val placeUrl: String,
    @SerialName("road_address_name") val roadAddress: String,
    @SerialName("x") val x: String,
    @SerialName("y") val y: String,
)