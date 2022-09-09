package com.bonustrack02.tp08goodprice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bonustrack02.tp08goodprice.databinding.ActivityDetailBinding;
import com.kakao.adfit.ads.AdListener;
import com.kakao.util.maps.helper.Utility;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    MapView mapView;
    MapPoint point;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkResult == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, 14);
        }

        binding.adview.setClientId("DAN-JSQBPajhzXXtakko");
        binding.adview.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailed(int i) {
                Log.i("AdView", "error : " + i);
            }

            @Override
            public void onAdClicked() {

            }
        });

        binding.adview.loadAd();

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mapView = new MapView(this);
        binding.containerMap.addView(mapView);

        Intent intent = getIntent();

        String img = intent.getStringExtra("img");
        ImageLoader imageLoader = ImageLoaderSingleTone.getImageLoader(this);
        binding.detailImg.setImageUrl(img, imageLoader);

        String name = intent.getStringExtra("name");
        binding.detailTextName.setText(name);

        String addr = intent.getStringExtra("addr");
        binding.detailTextAddr.setText(addr);
        binding.detailTextAddr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipData clipData = ClipData.newPlainText("주소", binding.detailTextAddr.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(DetailActivity.this, "주소가 복사되었습니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        binding.detailBtnMap.setOnClickListener(view -> {
            Uri uri = Uri.parse("kakaomap://look?p=" + latitude + "," + longitude);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(uri);
            startActivity(mapIntent);
        });

        String phone = intent.getStringExtra("phone");
        phone = phone.replace(" ", "");
        if (phone.contains("02-")) {
            binding.detailBtnPhone.setText(phone);
        } else {
            binding.detailBtnPhone.setText("02-" + phone);
        }


        binding.detailBtnPhone.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);

            Uri uri = Uri.parse("tel:" + binding.detailBtnPhone.getText().toString());
            callIntent.setData(uri);
            startActivity(callIntent);
        });

        String pride = intent.getStringExtra("pride");
        binding.detailTextPride.setText(pride);

        setPoint();
        mapView.setMapCenterPointAndZoomLevel(point, 3, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(binding.detailTextName.getText().toString());
        marker.setTag(0);
        marker.setMapPoint(point);
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.adview != null) binding.adview.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (binding.adview != null) binding.adview.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.adview != null) binding.adview.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 14) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치정보 사용 허가됨", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "위치정보 사용 거부됨", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void setPoint() {

        String address = binding.detailTextAddr.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 3);

            StringBuffer buffer = new StringBuffer();
            for (Address addr : addressList) {
                double lat = addr.getLatitude();
                double lng = addr.getLongitude();

                buffer.append(lat + ", " + lng + "\n");
            }

            latitude = addressList.get(0).getLatitude();
            longitude = addressList.get(0).getLongitude();

            point = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}