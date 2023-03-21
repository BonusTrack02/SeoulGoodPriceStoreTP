package com.bonustrack02.tp08goodprice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bonustrack02.tp08goodprice.databinding.RecyclerItemBinding

class RecyclerAdapter(val context: Context, var items: MutableList<Shop>): Adapter<RecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: RecyclerItemBinding = RecyclerItemBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                val item = items[adapterPosition]

                val intent = Intent(context, DetailActivity::class.java)
                intent
                    .putExtra("img", item.shopImage)
                    .putExtra("name", item.shopName)
                    .putExtra("addr", item.shopAddress)
                    .putExtra("phone", item.shopNumber)
                    .putExtra("pride", item.shopPride)

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val imageLoader = ImageLoaderSingleTone.getImageLoader(context)
        holder.binding.recyclerImgShop.setImageUrl(item.shopImage, imageLoader)
        holder.binding.recyclerTextTitle.text = item.shopName
        holder.binding.recyclerTextAddr.text = item.shopAddress
        holder.binding.recyclerTextTel.text = item.shopNumber

        Log.d("Tag", item.shopName)
    }

    override fun getItemCount(): Int = items.size
}