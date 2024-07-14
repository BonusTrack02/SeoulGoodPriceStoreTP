package com.bonustrack02.tp08goodprice

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bonustrack02.tp08goodprice.databinding.ActivityDetailBinding
import com.bonustrack02.tp08goodprice.network.ResponseKeyword
import com.bonustrack02.tp08goodprice.network.RetrofitHelper
import com.bonustrack02.tp08goodprice.network.RetrofitService
import com.bumptech.glide.Glide
import com.kakao.adfit.ads.AdListener
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.LocationTemplate
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val clientId = BuildConfig.SMALLBANNERCLIENTID
    private var mapView: MapView? = null
    private var point: MapPoint? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val retrofit = RetrofitHelper.getInstance("https://dapi.kakao.com")
    private val retrofitService = retrofit.create(RetrofitService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (checkResult == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, 14)
        }

        binding.adViewKakao.setClientId(clientId)
        binding.adViewKakao.setAdListener(object : AdListener {
            override fun onAdLoaded() {}
            override fun onAdFailed(i: Int) {
                Log.e("AdView", "error : $i")
            }
            override fun onAdClicked() {}
        })
        binding.adViewKakao.loadAd()

        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        mapView = MapView(this)
        binding.containerMap.addView(mapView)
        val intent = intent
        val img = intent.getStringExtra("img")
        Glide.with(this).load(img).into(binding.imgDetail)
        val name = intent.getStringExtra("name")
        binding.txtDetailShopName.text = name
        val address = intent.getStringExtra("addr")
        setPoint(address ?: "")
        binding.txtDetailShopAddr.text = address
        binding.txtDetailShopAddr.setOnLongClickListener {
            val clipData = ClipData.newPlainText("주소", binding.txtDetailShopAddr.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this@DetailActivity, R.string.copy_address, Toast.LENGTH_SHORT).show()
            true
        }

        binding.btnDetailKakaoMap.setOnClickListener { view: View? ->
            val uri = Uri.parse("kakaomap://look?p=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW)
            mapIntent.data = uri
            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.map_not_installed, Toast.LENGTH_SHORT).show()
            }
        }

        var phone = intent.getStringExtra("phone")
        phone = phone!!.replace(" ", "")
        if (
            phone.startsWith("02-")
            || phone.startsWith("0507-")
            || phone.startsWith("070-")
        ) {
            binding.btnDetailShopPhoneNumber.text = phone
        } else if (phone.isEmpty()) {
            binding.btnDetailShopPhoneNumber.visibility = INVISIBLE
        } else {
            binding.btnDetailShopPhoneNumber.text = "02-$phone"
        }

        binding.btnDetailShopPhoneNumber.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            val uri = Uri.parse("tel:" + binding.btnDetailShopPhoneNumber.text.toString())
            callIntent.data = uri
            startActivity(callIntent)
        }

        val pride = intent.getStringExtra("pride")
        binding.txtDetailShopPride.text = if (pride.isNullOrEmpty() || pride == "null") "" else pride
        mapView!!.setMapCenterPointAndZoomLevel(point, 3, true)
        mapView!!.zoomIn(true)
        mapView!!.zoomOut(true)
        val marker = MapPOIItem()
        marker.itemName = binding.txtDetailShopName.text.toString()
        marker.tag = 0
        marker.mapPoint = point
        marker.markerType = MapPOIItem.MarkerType.RedPin
        mapView!!.addPOIItem(marker)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                val call = retrofitService.getLocationByKakaoKeyword(binding.txtDetailShopName.text.toString())
                if (ShareClient.instance.isKakaoTalkSharingAvailable(this)) { // 카카오톡 쉐어링이 가능
                    call.enqueue(object : Callback<ResponseKeyword> {
                        override fun onResponse(
                            call: Call<ResponseKeyword>,
                            response: Response<ResponseKeyword>
                        ) {
                            try {
                                if (response.body()!!.placeList[0].address.contains("서울")) {
                                    val link = Link(
                                        mobileWebUrl = response.body()!!.placeList[0].placeUrl
                                    )
                                    val content = Content(
                                        title = response.body()!!.placeList[0].placeName,
                                        description = response.body()!!.placeList[0].roadAddress,
                                        imageUrl = intent.getStringExtra("img")!!,
                                        link = link
                                    )
                                    val defaultTemplate = LocationTemplate(
                                        address = response.body()!!.placeList[0].roadAddress,
                                        addressTitle = response.body()!!.placeList[0].placeName,
                                        content = content
                                    )
                                    ShareClient.instance.shareDefault(this@DetailActivity, defaultTemplate) { sharingResult, error ->
                                        if (error != null) {
                                            Log.e("Kakao sharing", "failed")
                                        } else if (sharingResult != null) {
                                            Log.d("Kakao sharing", "success")
                                            startActivity(sharingResult.intent)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@DetailActivity,
                                    "카카오맵에서 장소를 찾을 수 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseKeyword>, t: Throwable) {
                            Log.e("retrofit fail", "${t.stackTrace}")
                        }

                    })

                } else { // 불가능
                    Toast.makeText(this, "카카오톡 공유를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onResume() {
        super.onResume()
        binding.adViewKakao.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.adViewKakao.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adViewKakao.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 14) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치정보 사용 허가됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "위치정보 사용 거부됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPoint(address: String) {
        val geocoder = Geocoder(this, Locale.KOREA)
        try {
            val addressList = geocoder.getFromLocationName(address, 3)
            val buffer = StringBuffer()
            for (addr in addressList!!) {
                val lat = addr.latitude
                val lng = addr.longitude
                buffer.append("$lat, $lng\n")
            }
            latitude = addressList[0].latitude
            longitude = addressList[0].longitude
            point = MapPoint.mapPointWithGeoCoord(latitude, longitude)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}