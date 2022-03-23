package com.bonustrack02.tp08goodprice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    ImageView detailImg;
    TextView detailTextName, detailTextAddr, detailTextPhone, detailTextPride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailImg = findViewById(R.id.detail_img);
        detailTextName = findViewById(R.id.detail_text_name);
        detailTextAddr = findViewById(R.id.detail_text_addr);
        detailTextPhone = findViewById(R.id.detail_text_phone);
        detailTextPride = findViewById(R.id.detail_text_pride);

        Intent intent = getIntent();

        String img = intent.getStringExtra("img");
        Glide.with(this).load(img).into(detailImg);

        String name = intent.getStringExtra("name");
        detailTextName.setText(name);

        String addr = intent.getStringExtra("addr");
        detailTextAddr.setText(addr);

        String phone = intent.getStringExtra("phone");
        detailTextPhone.setText(phone);

        String pride = intent.getStringExtra("pride");
        detailTextPride.setText(pride);
    }
}