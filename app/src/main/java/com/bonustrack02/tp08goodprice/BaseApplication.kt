package com.bonustrack02.tp08goodprice

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class BaseApplication : Application() {
    private val kakaoAppKey = BuildConfig.KAKAO_APP_KEY
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, kakaoAppKey)
    }
}