package com.bonustrack02.tp08goodprice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonustrack02.tp08goodprice.databinding.FragmentKoreanBinding
import com.bonustrack02.tp08goodprice.network.RetrofitHelper
import com.bonustrack02.tp08goodprice.network.RetrofitResponse
import com.bonustrack02.tp08goodprice.network.RetrofitService
import com.bonustrack02.tp08goodprice.network.Shop
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KoreanFragment: Fragment() {
    private val binding: FragmentKoreanBinding by lazy { FragmentKoreanBinding.inflate(layoutInflater) }
    private val items = mutableListOf<Shop>()
    private val apiKey = BuildConfig.APIKEYSEOUL
    private var startIndex = 1
    private var endIndex = 30
    private var totalCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.adapter = RecyclerAdapter(requireContext(), items)
        getStoreListUsingRetrofit(startIndex, endIndex)
        binding.progressbar.visibility = View.VISIBLE

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)

                if (lastItemPosition == totalItemCount) {
                    this@KoreanFragment.startIndex += 30
                    this@KoreanFragment.endIndex += 30
                    getStoreListUsingRetrofit(this@KoreanFragment.startIndex, this@KoreanFragment.endIndex)

                    if (totalCount == items.size) Snackbar.make(context!!, binding.snackbarContainer, "마지막 리스트입니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStoreListUsingRetrofit(startIndex: Int, endIndex: Int) {
        val retrofitService = RetrofitHelper
            .getInstance("http://openapi.seoul.go.kr:8088")
            .create(RetrofitService::class.java)

        val call = retrofitService.getStoreJson(apiKey, startIndex, endIndex, "001")
        call.enqueue(object : Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                response.body()!!.responseItem.ShopList.forEach {
                    if (items.contains(it)) return@forEach
                    items.add(it)
                }
                binding.recycler.adapter?.notifyDataSetChanged()
                Log.d("recycler size", "${items.size}")
                totalCount = response.body()!!.responseItem.totalCount
                binding.progressbar.visibility = View.GONE
            }

            override fun onFailure(
                call: Call<RetrofitResponse>,
                t: Throwable
            ) {
                Log.e("retrofit error", "${t.stackTrace}")
            }
        })
    }
}