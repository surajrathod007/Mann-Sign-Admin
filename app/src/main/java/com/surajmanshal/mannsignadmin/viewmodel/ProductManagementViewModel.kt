package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementViewModel : ViewModel() {
    private val repository = Repository()
    private val _materials = MutableLiveData<List<Material>>()
    val materials : LiveData<List<Material>> get() = _materials
    private val _languages = MutableLiveData<List<Language>>()
    val languages  : LiveData<List<Language>> get() = _languages
    private val _subCategories = MutableLiveData<List<SubCategory>>()
    val subCategories: LiveData<List<SubCategory>> get() = _subCategories

    suspend fun setupViewModelDataMembers(){
        CoroutineScope(Dispatchers.IO).launch {
            getMaterials()
        }
        CoroutineScope(Dispatchers.IO).launch {
            getLanguages()
        }
        CoroutineScope(Dispatchers.IO).launch {
            getSubCategories()
        }
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

}