package com.bonustrack02.tp08goodprice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonustrack02.tp08goodprice.adapters.ShopItemAdapter
import com.bonustrack02.tp08goodprice.adapters.ShopItemDecoration
import com.bonustrack02.tp08goodprice.databinding.FragmentJapaneseBinding
import com.bonustrack02.tp08goodprice.network.RetrofitHelper
import com.bonustrack02.tp08goodprice.network.RetrofitResponse
import com.bonustrack02.tp08goodprice.network.RetrofitService
import com.bonustrack02.tp08goodprice.network.Shop
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JapaneseFragment : Fragment() {
    private val binding: FragmentJapaneseBinding by lazy {
        FragmentJapaneseBinding.inflate(
            layoutInflater
        )
    }
    private val shopItemAdapter = ShopItemAdapter()
    private val shopList = mutableListOf<Shop>()
    private val apiKey = BuildConfig.API_KEY_SEOUL
    private var startIndex = 1
    private var endIndex = 30
    private var totalCount = 0
    private val retrofitService = RetrofitHelper
        .getInstance("http://openapi.seoul.go.kr:8088")
        .create(RetrofitService::class.java)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            binding.recycler.adapter = shopItemAdapter
            binding.recycler.setHasFixedSize(true)
            binding.recycler.addItemDecoration(ShopItemDecoration())
            shopItemAdapter.submitList(getShopList(startIndex, endIndex)) {
                binding.progressbar.visibility = View.GONE
            }

            binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    lifecycleScope.launch {
                        val lastItemPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)

                        if (lastItemPosition == totalItemCount) {
                            binding.progressbar.visibility = View.VISIBLE
                            startIndex += 30
                            endIndex += 30
                            shopItemAdapter.submitList(getShopList(startIndex, endIndex)) {
                                binding.progressbar.visibility = View.GONE
                            }

                            if (totalCount == shopList.size) Snackbar.make(
                                requireContext(),
                                binding.snackbarContainer,
                                getString(R.string.snackbar_msg_last_item),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        }
    }

    private suspend fun getShopList(startIndex: Int, endIndex: Int): MutableList<Shop> =
        try {
            val response = retrofitService.getShopListJson(apiKey, startIndex, endIndex, "003")
            if (response.isSuccessful) {
                totalCount = response.body()?.responseItem?.totalCount ?: -1
                shopList.addAll(response.body()?.responseItem?.shopList ?: mutableListOf())
                shopList.toMutableList()
            } else {
                println(response.errorBody())
                throw Exception("API Request failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
}