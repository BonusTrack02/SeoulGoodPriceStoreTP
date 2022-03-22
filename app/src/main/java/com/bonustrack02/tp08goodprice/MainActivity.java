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

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
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

    void loadApiData() {
        Thread thread = new Thread() {

            int startIndex = 1, endIndex = 50, dirCode = 001;

            String apiAddress = "http://openapi.seoul.go.kr:8088/"
                    + apiKey + "/xml/ListPriceModelStoreService/"
                    + startIndex+ "/" + endIndex + "/" + dirCode + "/";

            ProgressBar progressBar = findViewById(R.id.progressbar);

            @Override
            public void run() {
                try {
                    URL url = new URL(apiAddress);

                    InputStream inputStream = url.openStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(reader);

                    int eventType = parser.getEventType();

                    Item item = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;

                            case XmlPullParser.START_TAG:
                                String startTag = parser.getName();
                                if (startTag.equals("row")) {
                                    item = new Item();
                                    Log.i("Tag", "row");
                                } else if (startTag.equals("SH_NAME")) {
                                    parser.next();
                                    if (item != null) item.name = parser.getText();
                                } else if (startTag.equals("SH_ADDR")) {
                                    parser.next();
                                    if (item != null) item.address = parser.getText();
                                } else if (startTag.equals("SH_PHONE")) {
                                    parser.next();
                                    if (item != null) item.phone = parser.getText();
                                } else if (startTag.equals("SH_PHOTO")) {
                                    parser.next();
                                    if (item != null) item.imgShop = parser.getText();
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                String endTag = parser.getName();
                                if (endTag.equals("row")) if (item != null) items.add(item);
                                break;

                            case XmlPullParser.TEXT:
                                break;
                        }
                        eventType = parser.next();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            Log.i("Tag", "" + items.size());
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}