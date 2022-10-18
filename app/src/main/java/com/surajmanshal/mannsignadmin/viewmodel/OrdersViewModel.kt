package com.surajmanshal.mannsignadmin.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersViewModel : ViewModel() {
    //lateinit var  repository : Repository

    private val _allOrders = MutableLiveData<List<Order>>(emptyList())
    val allOrders: LiveData<List<Order>> get() = _allOrders

    val isEmptyList = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(true)
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse

    companion object {
        val repository = Repository()
    }

    suspend fun setupViewModelDataMembers() {
        CoroutineScope(Dispatchers.IO).launch { getAllOrders() }
    }

    fun filterOrder(status: Int) {

        val list = _allOrders.value?.filter { it.orderStatus == status }
        _allOrders.value = list!!
    }

    fun refreshOrders(){
        _allOrders.postValue(_allOrders.value)
    }

    fun getAllOrders() {
        val v = repository.fetchAllOrders()
        v.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                response.body().let {
                    _allOrders.value = it
                    if (it.isNullOrEmpty()) {
                        isEmptyList.postValue(true)
                    }
                }
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {

                isLoading.postValue(false)
            }
        })
    }

    suspend fun updateOrder(order: Order) {

            try {
                val response = NetworkService.networkInstance.updateOrder(order)
                response.enqueue(object : Callback<SimpleResponse?> {
                    override fun onResponse(
                        call: Call<SimpleResponse?>,
                        response: Response<SimpleResponse?>
                    ) {
                        _serverResponse.postValue(response.body())

                        //this is not working
                        /*
                        CoroutineScope(Dispatchers.IO).launch {
                            getAllOrders()
                            refreshOrders()
                        }
                         */

                    }

                    override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                        _serverResponse.postValue(SimpleResponse(true, message = "Some error"))
                    }
                })
            }catch (e : Exception){
                _serverResponse.postValue(SimpleResponse(true, message = e.message.toString()))
            }



    }


}