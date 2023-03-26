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
import com.bonustrack02.tp08goodprice.databinding.FragmentChineseBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChineseFragment: Fragment() {
    private val binding: FragmentChineseBinding by lazy { FragmentChineseBinding.inflate(layoutInflater) }
    private val items = mutableListOf<Shop>()
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
        binding.recycler.adapter = RecyclerAdapter(requireContext(), items)

        getStoreListUsingRetrofit(startIndex, endIndex)

        binding.recycler.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)

                if (lastItemPosition == totalItemCount) {
                    this@ChineseFragment.startIndex += 30
                    this@ChineseFragment.endIndex += 30
                    getStoreListUsingRetrofit(this@ChineseFragment.startIndex, this@ChineseFragment.endIndex)

                    if (totalCount == items.size) Snackbar.make(context!!, binding.snackbarContainer, "마지막 리스트입니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getStoreListUsingRetrofit(startIndex: Int, endIndex: Int) {
        val retrofitService = RetrofitHelper.getInstance().create(RetrofitService::class.java)
        val call = retrofitService.getStoreJson(apiKey, startIndex, endIndex, "002")
        call.enqueue(object : Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                Log.d("retrofit warning", "${response.body()}")
                Log.d("retrofit result", "${response.raw()}")
                response.body()!!.responseItem.ShopList.forEach {
                    if (items.contains(it)) return@forEach
                    items.add(it)
                }
                binding.recycler.adapter?.notifyDataSetChanged()
                Log.d("recycler size", "${items.size}")
                totalCount = response.body()!!.responseItem.totalCount
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