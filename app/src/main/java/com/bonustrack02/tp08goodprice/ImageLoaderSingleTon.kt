package com.bonustrack02.tp08goodprice

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageLoader.ImageCache
import com.android.volley.toolbox.Volley

class ImageLoaderSingleTon private constructor() {
    companion object {
        var imageLoader: ImageLoader? = null

        fun getImageLoader(context: Context) : ImageLoader {
            if (imageLoader == null) {
                val requestQueue: RequestQueue = Volley.newRequestQueue(context)
                imageLoader = ImageLoader(requestQueue, object : ImageCache {
                    val cache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(100)

                    override fun getBitmap(url: String?): Bitmap? = cache.get(url)

                    override fun putBitmap(url: String?, bitmap: Bitmap?) {
                        cache.put(url, bitmap)
                    }
                })
            }
            return imageLoader as ImageLoader
        }
    }
}