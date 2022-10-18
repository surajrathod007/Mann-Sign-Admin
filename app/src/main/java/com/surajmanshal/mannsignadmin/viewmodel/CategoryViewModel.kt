package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel:ViewModel() {
    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _categories = MutableLiveData<MutableList<Category>>(mutableListOf())
    val categories: LiveData<MutableList<Category>> get() = _categories
    private val _isDeleting = MutableLiveData<Boolean>()
    val isDeleting: LiveData<Boolean> get() = _isDeleting
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE
    private val _deletionCategory = MutableLiveData<Category>()
    val deletionCategory : LiveData<Category> get() = _deletionCategory     // CATEGORY DELETION


    fun getCategories(){
        val response = repository.fetchCategory()
        response.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                print(response.body().toString())

                response.body()?.let {
                    clearCategories()
                    addAllCategories(it)
                    refresh()}

            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                print(t.toString())
            }
        })
    }
    fun onDeleteAlert(category: Category){
        _deletionCategory.value = category
        _isDeleting.value = true
    }
    fun onDeletionCancelOrDone(){
        _isDeleting.value = false
    }

    suspend fun deleteCategory(category: Category){
        try {
            val response = repository.deleteCategory(category.id)
            _serverResponse.postValue(response)
            deleteFromCategories(category)

        }catch (e : Exception){
            println("$e")
        }
    }

    private fun addAllCategories(list : List<Category>){
        _categories.value?.addAll(list)
    }
    private fun clearCategories(){
        _categories.value?.clear()
    }
    private fun deleteFromCategories(category: Category){
        _categories.value?.remove(category)
        refresh()
    }
    private fun refresh(){
        _categories.postValue(categories.value)
    }
}