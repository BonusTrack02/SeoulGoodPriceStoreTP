package com.bonustrack02.tp08goodprice.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bonustrack02.tp08goodprice.DetailActivity
import com.bonustrack02.tp08goodprice.R
import com.bonustrack02.tp08goodprice.databinding.RecyclerShopItemBinding
import com.bonustrack02.tp08goodprice.network.Shop
import com.bumptech.glide.Glide

class ShopItemAdapter : ListAdapter<Shop, ShopItemAdapter.ShopViewHolder>(ShopItemComparator()) {
    inner class ShopViewHolder(itemView: View, private val context: Context) :
        ViewHolder(itemView) {
        val binding = RecyclerShopItemBinding.bind(itemView)

        fun bind(item: Shop) {
            binding.txtShopName.text = item.shopName
            binding.txtShopAddress.text = item.shopAddress
            println(item.shopNumber)
            Glide.with(context).load(item.shopImage).placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24).into(binding.imgShop)

            binding.root.setOnClickListener { v ->
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("img", item.shopImage)
                    putExtra("name", item.shopName)
                    putExtra("addr", item.shopAddress)
                    putExtra("phone", item.shopNumber)
                    putExtra("pride", item.shopPride)
                }

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_shop_item, parent, false)
        return ShopViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onViewDetachedFromWindow(holder: ShopViewHolder) {
        holder.binding.root.setOnClickListener(null)
    }
}

class ShopItemComparator : DiffUtil.ItemCallback<Shop>() {
    override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean =
        oldItem.shopId == newItem.shopId

    override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean =
        oldItem.shopName == newItem.shopName

}

/**
 * This ItemDecoration class always have 8dp size space
 * */
class ShopItemDecoration : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = 8
            bottom = 8
            left = 8
            right = 8
        }
    }
}