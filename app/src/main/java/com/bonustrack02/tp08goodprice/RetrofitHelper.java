package com.bonustrack02.tp08goodprice;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {

    public static Retrofit getInstance() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl("http://openapi.seoul.go.kr:8088");
        builder.addConverterFactory(ScalarsConverterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }
}
