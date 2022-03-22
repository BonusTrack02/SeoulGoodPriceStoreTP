package com.bonustrack02.tp08goodprice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JapaneseFragment extends Fragment {

    ArrayList<Item> items = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    ProgressBar progressBar;
    String apiKey = "4e4c586954776c7338365056587546";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_japanese, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progressbar);
        adapter = new RecyclerAdapter(getActivity(), items);
        recyclerView = view.findViewById(R.id.recycler);

        loadApiData();

        recyclerView.setAdapter(adapter);
    }

    void loadApiData() {
        Thread thread = new Thread() {

            @Override
            public void run() {
                int startIndex = 1, endIndex = 30;

                String apiAddress = "http://openapi.seoul.go.kr:8088/"
                        + apiKey + "/xml/ListPriceModelStoreService/"
                        + startIndex+ "/" + endIndex + "/003/";
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
                                getActivity().runOnUiThread(new Runnable() {
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null) adapter.notifyDataSetChanged();
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
