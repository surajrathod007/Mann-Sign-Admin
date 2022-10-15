package com.surajmanshal.mannsignadmin.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementViewModel : ViewModel() {
    private val repository = Repository()
    private val _sizes = MutableLiveData<List<Size>>()
    val sizes : LiveData<List<Size>> get() = _sizes
    private val _materials = MutableLiveData<List<Material>>()
    val materials : LiveData<List<Material>> get() = _materials
    private val _languages = MutableLiveData<List<Language>>()
    val languages  : LiveData<List<Language>> get() = _languages
    private val _subCategories = MutableLiveData<List<SubCategory>>()
    val subCategories: LiveData<List<SubCategory>> get() = _subCategories
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse

    suspend fun setupViewModelDataMembers(){
        CoroutineScope(Dispatchers.IO).launch { getSizes() }
        CoroutineScope(Dispatchers.IO).launch { getMaterials() }
        CoroutineScope(Dispatchers.IO).launch { getLanguages() }
        CoroutineScope(Dispatchers.IO).launch { getSubCategories() }
    }

    private suspend fun getSizes(){
        val response = repository.fetchSizes()
        println("Response is $response")
        response.enqueue(object : Callback<List<Size>> {
            override fun onResponse(call: Call<List<Size>>, response: Response<List<Size>>) {
                println("Inner Response is $response")
                response.body()?.let { _sizes.value = it }
            }
            override fun onFailure(call: Call<List<Size>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getMaterials(){
        val response = repository.fetchMaterials()
        println("Response is $response")
        response.enqueue(object : Callback<List<Material>> {
            override fun onResponse(call: Call<List<Material>>, response: Response<List<Material>>) {
                println("Inner Response is $response")
                response.body()?.let { _materials.value = it }
            }
            override fun onFailure(call: Call<List<Material>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getLanguages(){
        val response = repository.fetchLanguages()
        println("Response is $response")
        response.enqueue(object : Callback<List<Language>> {
            override fun onResponse(call: Call<List<Language>>, response: Response<List<Language>>) {
                println("Inner Response is $response")
                response.body()?.let { _languages.value = it }
            }
            override fun onFailure(call: Call<List<Language>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getSubCategories(){
        val response = repository.fetchSubCategories()
        println("Response is $response")
        response.enqueue(object : Callback<List<SubCategory>> {
            override fun onResponse(call: Call<List<SubCategory>>, response: Response<List<SubCategory>>) {
                println("Inner Response is $response")
                response.body()?.let { _subCategories.value = it }
            }
            override fun onFailure(call: Call<List<SubCategory>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun  addProduct(product: Product) {
        try {
            _serverResponse.value = repository.sendProduct(product)
        }catch (e : Exception){
            println("$e")
        }

       /* response.enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                println("Inner Response is $response")
                response.body()?.let { _serverResponse.value = it }
            }
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                println("Failure is $t")
            }
        })*/
    }
}