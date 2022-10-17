package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel:ViewModel() {
    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    fun getCategories(){
        val response = repository.fetchCategory()
        response.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                print(response.body().toString())
                _categories.value = response.body()
            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                print(t.toString())
            }
        })
    }
}