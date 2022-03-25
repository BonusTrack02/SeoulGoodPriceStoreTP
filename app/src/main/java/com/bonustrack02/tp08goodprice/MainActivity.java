package com.bonustrack02.tp08goodprice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Item> items = new ArrayList<>();

    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    ArrayList<Fragment> fragments = new ArrayList<>();

    String apiKey = "4e4c586954776c7338365056587546";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        fragments.add(new KoreanFragment());

        fragmentManager.beginTransaction().add(R.id.container, fragments.get(0)).commit();
        for (int i = 0; i < 3; i++) {
            fragments.add(null);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                for (int j = 0; j < 4; j++) {
                    if (fragments.get(j) != null) transaction.hide(fragments.get(j));
                }

                switch (item.getItemId()) {
                    case R.id.menu_bottom_korean:
                        transaction.show(fragments.get(0));
                        break;

                    case R.id.menu_bottom_chinese:
                        if (fragments.get(1) == null) {
                            fragments.set(1, new ChineseFragment());
                            transaction.add(R.id.container, fragments.get(1));
                        }
                        transaction.show(fragments.get(1));
                        break;

                    case R.id.menu_bottom_japanese:
                        if (fragments.get(2) == null) {
                            fragments.set(2, new JapaneseFragment());
                            transaction.add(R.id.container, fragments.get(2));
                        }
                        transaction.show(fragments.get(2));
                        break;

                    case R.id.menu_bottom_others:
                        if (fragments.get(3) == null) {
                            fragments.set(3, new OthersFragment());
                            transaction.add(R.id.container, fragments.get(3));
                        }
                        transaction.show(fragments.get(3));
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}