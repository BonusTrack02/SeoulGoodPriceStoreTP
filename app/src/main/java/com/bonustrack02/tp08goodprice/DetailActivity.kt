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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bonustrack02.tp08goodprice.databinding.ActivityDetailBinding
import com.kakao.adfit.ads.AdListener
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.IOException
import java.util.*

class DetailActivity : AppCompatActivity() {
    val binding: ActivityDetailBinding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val clientId = BuildConfig.SMALLBANNERCLIENTID
    var mapView: MapView? = null
    var point: MapPoint? = null
    var latitude = 0.0
    var longitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (checkResult == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, 14)
        }

        binding.adview.setClientId(clientId)
        binding.adview.setAdListener(object : AdListener {
            override fun onAdLoaded() {}
            override fun onAdFailed(i: Int) {
                Log.i("AdView", "error : $i")
            }

            override fun onAdClicked() {}
        })
        binding.adview.loadAd()
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        mapView = MapView(this)
        binding.containerMap.addView(mapView)
        val intent = intent
        val img = intent.getStringExtra("img")
        val imageLoader = ImageLoaderSingleTon.getImageLoader(this)
        binding.detailImg.setImageUrl(img, imageLoader)
        val name = intent.getStringExtra("name")
        binding.detailTextName.text = name
        val addr = intent.getStringExtra("addr")
        binding.detailTextAddr.text = addr
        binding.detailTextAddr.setOnLongClickListener {
            val clipData = ClipData.newPlainText("주소", binding.detailTextAddr.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this@DetailActivity, R.string.copy_address, Toast.LENGTH_SHORT).show()
            true
        }
        binding.detailBtnMap.setOnClickListener { view: View? ->
            val uri = Uri.parse("kakaomap://look?p=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW)
            mapIntent.data = uri
            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, R.string.map_not_installed, Toast.LENGTH_SHORT).show()
            }
        }
        var phone = intent.getStringExtra("phone")
        phone = phone!!.replace(" ", "")
        if (phone.contains("02-")) {
            binding.detailBtnPhone.text = phone
        } else {
            binding.detailBtnPhone.text = "02-$phone"
        }
        binding.detailBtnPhone.setOnClickListener { view: View? ->
            val callIntent = Intent(Intent.ACTION_DIAL)
            val uri = Uri.parse("tel:" + binding.detailBtnPhone.text.toString())
            callIntent.data = uri
            startActivity(callIntent)
        }
        val pride = intent.getStringExtra("pride")
        binding.detailTextPride.text = pride
        setPoint()
        mapView!!.setMapCenterPointAndZoomLevel(point, 3, true)
        mapView!!.zoomIn(true)
        mapView!!.zoomOut(true)
        val marker = MapPOIItem()
        marker.itemName = binding.detailTextName.text.toString()
        marker.tag = 0
        marker.mapPoint = point
        marker.markerType = MapPOIItem.MarkerType.RedPin
        mapView!!.addPOIItem(marker)
    }

    override fun onResume() {
        super.onResume()
        binding.adview.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.adview.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adview.destroy()
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

    fun setPoint() {
        val address = binding.detailTextAddr.text.toString()
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