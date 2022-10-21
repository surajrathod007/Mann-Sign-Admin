package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.DateFilter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.Transaction
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StatsViewModel : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse: LiveData<SimpleResponse> get() = _serverResponse

    private val _allOrders = MutableLiveData<List<Order>>(emptyList())
    val allOrders: LiveData<List<Order>> get() = _allOrders

    private val _dateFilter = MutableLiveData<DateFilter>()
    val dateFilter: LiveData<DateFilter> get() = _dateFilter


    suspend fun setupViewModelDataMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            getAllOrders()
            getAllTransactions()
        }
    }

    fun getAllTransactions() {

        try {
            val l = NetworkService.networkInstance.fetchAllTransactions()
            l.enqueue(object : Callback<List<Transaction>?> {
                override fun onResponse(
                    call: Call<List<Transaction>?>,
                    response: Response<List<Transaction>?>
                ) {
                    _transactions.postValue(response.body())
                }

                override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                    _serverResponse.postValue(SimpleResponse(true, "${t.message}"))
                }
            })
        } catch (e: Exception) {
            _serverResponse.postValue(SimpleResponse(true, "${e.message}"))
        }


    }

    fun getAllOrders() {
        val v = OrdersViewModel.repository.fetchAllOrders()
        v.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                response.body().let {
                    _allOrders.value = it
                }

            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {

            }
        })
    }

    suspend fun filterTransaction(d: DateFilter) {
        val f = NetworkService.networkInstance.filterTransaction(d)
        f.enqueue(object : Callback<List<Transaction>?> {
            override fun onResponse(
                call: Call<List<Transaction>?>,
                response: Response<List<Transaction>?>
            ) {
                _transactions.postValue(response.body())
                _serverResponse.postValue(SimpleResponse(true, response.body()!!.size.toString()))
            }

            override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}