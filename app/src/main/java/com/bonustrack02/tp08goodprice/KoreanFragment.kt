package com.bonustrack02.tp08goodprice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonustrack02.tp08goodprice.adapters.ShopItemAdapter
import com.bonustrack02.tp08goodprice.adapters.ShopItemDecoration
import com.bonustrack02.tp08goodprice.databinding.FragmentKoreanBinding
import com.bonustrack02.tp08goodprice.network.RetrofitHelper
import com.bonustrack02.tp08goodprice.network.RetrofitService
import com.bonustrack02.tp08goodprice.network.Shop
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class KoreanFragment : Fragment() {
    private val binding: FragmentKoreanBinding by lazy {
        FragmentKoreanBinding.inflate(
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
                        val currentLastItemPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        val lastItemPosition = recyclerView.adapter?.itemCount?.minus(1)

                        if (currentLastItemPosition == lastItemPosition) {
                            binding.progressbar.visibility = View.VISIBLE
                            startIndex += 30
                            endIndex = if (endIndex + 30 > totalCount) {
                                totalCount
                            } else {
                                endIndex + 30
                            }

                            if (endIndex == totalCount) {
                                binding.progressbar.visibility = View.GONE
                                Snackbar.make(
                                    requireContext(),
                                    binding.snackbarContainer,
                                    getString(R.string.snackbar_msg_last_item),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                cancel()
                            }

                            val tempList = getShopList(startIndex, endIndex)
                            if (tempList.isEmpty()) { // Something is wrong
                                binding.progressbar.visibility = View.GONE
                                cancel()
                            } else {
                                shopItemAdapter.submitList(tempList) {
                                    binding.progressbar.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private suspend fun getShopList(startIndex: Int, endIndex: Int): MutableList<Shop> =
        try {
            val response = retrofitService.getShopListJson(apiKey, startIndex, endIndex, "001")
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
            Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show()
            mutableListOf()
        }
}