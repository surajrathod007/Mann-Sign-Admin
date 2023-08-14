package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.AdBanner
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdBannerViewModel : ViewModel() {

    val server = NetworkService.networkInstance


    private val _adBanners = MutableLiveData<List<AdBanner>>()
    val adBanners: LiveData<List<AdBanner>> get() = _adBanners
    fun setAdBanners(value: List<AdBanner>) {
        _adBanners.postValue(value)
    }

    // Example of getting all banners
    fun getBanners() {
        val getAllBannersCall = server.getAllBanners()
        getAllBannersCall.enqueue(object : Callback<List<AdBanner>> {
            override fun onResponse(
                call: Call<List<AdBanner>>,
                response: Response<List<AdBanner>>
            ) {
                if (response.isSuccessful) {
                    val bannerList = response.body()
                    bannerList?.let {
                        _adBanners.postValue(it)
                    }
                } else {
                    // todo : Handle the error
                }
            }

            override fun onFailure(call: Call<List<AdBanner>>, t: Throwable) {
                // todo : Handle the failure
            }
        })
    }

    fun updateBanner(banner: AdBanner) {
        val updateBannerCall = server.updateBanner(banner)
        updateBannerCall.enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful) {
                    val simpleResponse = response.body()
                    // todo : Handle the response
                } else {
                    // todo : Handle the error
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                // todo : Handle the failure
            }
        })
    }

    // Example of deleting a banner
    fun deleteBanner(bannerToDelete: AdBanner) {
        val deleteBannerCall = server.deleteBanner(bannerToDelete)
        deleteBannerCall.enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(
                call: Call<SimpleResponse>,
                response: Response<SimpleResponse>
            ) {
                if (response.isSuccessful) {
                    val simpleResponse = response.body()
                    simpleResponse?.let {
                        getBanners()
                    }
                } else {
                    // todo : Handle the error
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                // todo : Handle the failure
            }
        })
    }


}