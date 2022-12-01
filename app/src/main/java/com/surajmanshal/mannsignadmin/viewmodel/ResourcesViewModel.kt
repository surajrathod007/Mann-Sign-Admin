package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel.Companion.repository

import com.surajmanshal.response.SimpleResponse
import okhttp3.MultipartBody

class ResourcesViewModel : ViewModel() {

    private val repository = Repository()
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse


    suspend fun sendFont(part: MultipartBody.Part){
        try {
            val response = repository.uploadFontFile(part)
            _serverResponse.postValue(response)

        }catch (e : Exception){
            println("$e ${serverResponse.value?.message}")
        }
    }
}