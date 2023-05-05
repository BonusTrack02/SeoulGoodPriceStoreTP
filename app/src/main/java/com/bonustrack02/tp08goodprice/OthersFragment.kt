package com.bonustrack02.tp08goodprice

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bonustrack02.tp08goodprice.databinding.FragmentOthersBinding
import com.bonustrack02.tp08goodprice.network.RetrofitHelper
import com.bonustrack02.tp08goodprice.network.RetrofitResponse
import com.bonustrack02.tp08goodprice.network.RetrofitService
import com.bonustrack02.tp08goodprice.network.Shop
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OthersFragment: Fragment() {
    val binding: FragmentOthersBinding by lazy { FragmentOthersBinding.inflate(layoutInflater) }
    var items = mutableListOf<Shop>()
    private val apiKey = BuildConfig.APIKEYSEOUL
    var startIndex = 1
    var endIndex = 30
    var totalCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = RecyclerAdapter(requireActivity(), items)
        getStoreListUsingRetrofit(startIndex, endIndex)
        binding.progressbar.visibility = View.VISIBLE

        binding.recycler.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)

                if (lastItemPosition == totalItemCount) {
                    this@OthersFragment.startIndex += 30
                    this@OthersFragment.endIndex += 30
                    getStoreListUsingRetrofit(this@OthersFragment.startIndex, this@OthersFragment.endIndex)

                    if (totalCount == items.size) Snackbar.make(context!!, binding.snackbarContainer, "마지막 리스트입니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStoreListUsingRetrofit(startIndex: Int, endIndex: Int) {
        val retrofitService = RetrofitHelper
            .getInstance("http://openapi.seoul.go.kr:8088")
            .create(RetrofitService::class.java)

        val call = retrofitService.getStoreJson(apiKey, startIndex, endIndex, "004")
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