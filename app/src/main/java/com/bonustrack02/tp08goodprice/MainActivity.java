package com.bonustrack02.tp08goodprice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

    String apiKey = "4e4c586954776c7338365056587546";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        adapter = new RecyclerAdapter(this, items);
        recyclerView.setAdapter(adapter);

        loadApiData();
    }

    void loadApiData() {
        Thread thread = new Thread() {

            int startIndex = 1, endIndex = 50;

            String apiAddress = "http://openapi.seoul.go.kr:8088/"
                    + apiKey + "/xml/ListPriceModelStoreService/"
                    + startIndex+ "/" + endIndex + "/001/";

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