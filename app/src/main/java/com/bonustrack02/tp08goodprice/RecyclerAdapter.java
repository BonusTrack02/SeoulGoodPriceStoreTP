package com.bonustrack02.tp08goodprice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    Context context;
    ArrayList<Item> items;

    public RecyclerAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Item item = items.get(position);

        ImageLoader imageLoader = ImageLoaderSingleTone.getImageLoader(context);
        holder.recyclerImgShop.setImageUrl(item.imgShop, imageLoader);

        holder.recyclerTextTitle.setText(item.name);
        holder.recyclerTextAddr.setText(item.address);
        holder.recyclerTextTel.setText(item.phone);

        Log.i("Tag", "" + item.name);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        NetworkImageView recyclerImgShop;
        TextView recyclerTextTitle, recyclerTextAddr, recyclerTextTel;

        public Holder(@NonNull View itemView) {
            super(itemView);

            recyclerImgShop = itemView.findViewById(R.id.recycler_img_shop);
            recyclerTextTitle = itemView.findViewById(R.id.recycler_text_title);
            recyclerTextAddr = itemView.findViewById(R.id.recycler_text_addr);
            recyclerTextTel = itemView.findViewById(R.id.recycler_text_tel);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Item item = items.get(position);

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("img", item.imgShop);
                intent.putExtra("name", item.name);
                intent.putExtra("addr", item.address);
                intent.putExtra("phone", item.phone);
                intent.putExtra("pride", item.pride);

                context.startActivity(intent);
            });
        }
    }
}
