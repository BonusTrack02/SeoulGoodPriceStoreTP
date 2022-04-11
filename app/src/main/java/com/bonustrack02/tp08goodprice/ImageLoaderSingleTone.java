package com.bonustrack02.tp08goodprice;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ImageLoaderSingleTone {
    static ImageLoader imageLoader;


    public static ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
                LruCache<String, Bitmap> cache = new LruCache<>(100);

                @Nullable
                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        }
        return imageLoader;
    }
}
