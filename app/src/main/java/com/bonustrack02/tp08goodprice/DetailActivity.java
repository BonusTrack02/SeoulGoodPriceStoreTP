package com.bonustrack02.tp08goodprice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonustrack02.tp08goodprice.databinding.ActivityDetailBinding;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String img = intent.getStringExtra("img");
        Glide.with(this).load(img).into(binding.detailImg);

        String name = intent.getStringExtra("name");
        binding.detailTextName.setText(name);

        String addr = intent.getStringExtra("addr");
        binding.detailTextAddr.setText(addr);

        String phone = intent.getStringExtra("phone");
        binding.detailTextPhone.setText(phone);
        binding.detailTextPhone.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);

            Uri uri = Uri.parse("tel:" + binding.detailTextPhone.getText().toString());
            callIntent.setData(uri);
            startActivity(callIntent);
        });

        String pride = "자랑거리\n\n" + intent.getStringExtra("pride");
        binding.detailTextPride.setText(pride);
    }
}