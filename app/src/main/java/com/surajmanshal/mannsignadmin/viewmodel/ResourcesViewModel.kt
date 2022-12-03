package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.repository.Repository

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

    suspend fun sendSize(size : Size){
        val response = repository.insertSize(size)
        _serverResponse.postValue(response)
    }

    suspend fun sendMaterial(material : Material){
        val response = repository.insertMaterial(material)
        _serverResponse.postValue(response)
    }

    suspend fun sendLanguage(language : Language){
        val response = repository.insertLanguage(language)
        _serverResponse.postValue(response)
    }
}