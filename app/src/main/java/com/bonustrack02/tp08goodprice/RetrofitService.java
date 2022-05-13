package com.bonustrack02.tp08goodprice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitService {
    @GET("/{key}/json/ListPriceModelStoreService/{startIndex}/{endIndex}")
    Call<Item> getStoreJson(@Path("key") String key, @Path("startIndex") int startIndex, @Path("endIndex") int endIndex);
}
