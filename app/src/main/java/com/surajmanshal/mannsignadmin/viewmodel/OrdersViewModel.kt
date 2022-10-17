package com.surajmanshal.mannsignadmin.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersViewModel : ViewModel() {
    //lateinit var  repository : Repository

    private val _allOrders = MutableLiveData<List<Order>>(emptyList())
    val allOrders : LiveData<List<Order>> get() = _allOrders

    val isEmptyList = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(true)

    companion object{
        val repository = Repository()
    }

    suspend fun setupViewModelDataMembers(){
        CoroutineScope(Dispatchers.IO).launch { getAllOrders() }
    }

    fun filterOrder(status : Int){

        val list = _allOrders.value?.filter { it.orderStatus == status }
        _allOrders.value = list!!
    }

    fun getAllOrders() {
        val v = repository.fetchAllOrders()
        v.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                response.body().let {
                    _allOrders.value = it
                    if(it.isNullOrEmpty()){
                        isEmptyList.postValue(true)
                    }
                }
                isLoading.postValue(false)
            }
            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
                TODO("Not yet implemented")
                isLoading.postValue(false)
            }
        })
    }

}