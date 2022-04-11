package com.bonustrack02.tp08goodprice;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bonustrack02.tp08goodprice.databinding.FragmentKoreanBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class KoreanFragment extends Fragment {

    FragmentKoreanBinding binding;

    ArrayList<Item> items = new ArrayList<>();
    RecyclerAdapter adapter;
    String apiKey = "4e4c586954776c7338365056587546";
    String spinnerText = "전체";
    int startIndex = 1, endIndex = 30;
    SearchView searchView;
    ArrayAdapter arrayAdapter;
    String[] arrRegion;
    int listTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentKoreanBinding.inflate(inflater, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.region, R.layout.spinner);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.spinner.setAdapter(arrayAdapter);

        arrRegion = getResources().getStringArray(R.array.region);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //i : position
                spinnerText = arrRegion[i];
                items.clear();
                startIndex = 1;
                endIndex = 30;
                loadApiData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        adapter = new RecyclerAdapter(getActivity(), items);

        binding.recycler.setAdapter(adapter);

        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemSize = layoutManager.getItemCount();
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= totalItemSize - 1) {
                    startIndex += 30;
                    endIndex += 30;
                    loadApiData();
                    if (listTotal == totalItemSize)
                        Snackbar.make(getActivity(), binding.snackbarContainer, "마지막 리스트입니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    void loadApiData() {
        Thread thread = new Thread() {

            int eventType = 200;

            @Override
            public void run() {

                String apiAddress = "http://openapi.seoul.go.kr:8088/"
                        + apiKey + "/xml/ListPriceModelStoreService/"
                        + startIndex+ "/" + endIndex + "/001/";

                Log.i("aaaa", apiAddress);
                try {
                    URL url = new URL(apiAddress);

                    InputStream inputStream = url.openStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(reader);

                    eventType = parser.getEventType();

                    Item item = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.progressbar.setVisibility(View.VISIBLE);
                                        Log.i("bar", "progressbar!");
                                    }
                                });
                                break;

                            case XmlPullParser.START_TAG:
                                String startTag = parser.getName();
                                if (startTag == null) {
                                    Log.i("aaaa", "null");
                                    eventType = parser.next();
                                    continue;
                                }
                                if (startTag.equals("row")) {
                                    item = new Item();
                                    Log.i("Tag", "row");
                                } else if (startTag.equals("list_total_count")) {
                                    parser.next();
                                    listTotal = Integer.parseInt(parser.getText());
                                } else if (startTag.equals("SH_NAME")) {
                                    parser.next();
                                    if (item != null) item.name = parser.getText();
                                } else if (startTag.equals("SH_ADDR")) {
                                    parser.next();
                                    if (item != null) item.address = parser.getText();
                                } else if (startTag.equals("SH_PRIDE")) {
                                    parser.next();
                                    if (item != null) {
                                        item.pride = parser.getText();
                                        if (item.pride == null || item.pride.equals("null")) item.pride = "자료 없음";
                                    }
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
                                if (endTag.equals("row")) {
                                    Log.i("address", item.address + "");
                                    if (item != null) {
                                        if (spinnerText.equals("전체") || item.address.contains(spinnerText)) {
                                            Log.i("itemaddress", item.address + " : " + spinnerText);
                                            items.add(item);
                                        }
                                    }
                                }
                                break;

                            case XmlPullParser.TEXT:
                                break;
                        }
                        eventType = parser.next();
                    }

                    if (listTotal >= 30 && items.size() < 30) {
                        startIndex += 30;
                        endIndex += 30;
                        if (listTotal < endIndex) {
                            eventType = XmlPullParser.END_DOCUMENT;
                            Log.i("dkanrjsk", "end");
                        } else loadApiData();

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter != null) adapter.notifyDataSetChanged();
                                Log.i("Tag", "" + items.size());
                                binding.progressbar.setVisibility(View.GONE);
                            }
                        });
                    }

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.option, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("가게 이름을 입력하세요");
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
