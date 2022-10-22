package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.DateFilter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.Transaction
import com.surajmanshal.mannsignadmin.data.model.TransactionItem
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

    private val _transactionItems = MutableLiveData<List<TransactionItem>>()
    val transactionItems : LiveData<List<TransactionItem>> get() = _transactionItems

    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse: LiveData<SimpleResponse> get() = _serverResponse

    private val _allOrders = MutableLiveData<List<Order>>(emptyList())
    val allOrders: LiveData<List<Order>> get() = _allOrders

    private val _dateFilter = MutableLiveData<DateFilter>()
    val dateFilter: LiveData<DateFilter> get() = _dateFilter

    val isLoading = MutableLiveData<Boolean>(true)


    suspend fun setupViewModelDataMembers() {
        CoroutineScope(Dispatchers.IO).launch {
            //isLoading.postValue(true)
            getAllOrders()
            getAllTransactions()
        }
    }

    fun getAllTransactions() {

        isLoading.postValue(true)

        try {
            val l = NetworkService.networkInstance.fetchAllTransactions()
            l.enqueue(object : Callback<List<Transaction>?> {
                override fun onResponse(
                    call: Call<List<Transaction>?>,
                    response: Response<List<Transaction>?>
                ) {
                    val l = response.body()
                    //_transactions.postValue(response.body())
                    val lst = mutableListOf<TransactionItem>()

                    if(!l.isNullOrEmpty()){
                        l.forEach {
                            lst.add(TransactionItem(it))
                        }
                    }

                    _transactionItems.postValue(lst)
                    isLoading.postValue(false)

                }

                override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                    _serverResponse.postValue(SimpleResponse(true, "${t.message}"))
                    isLoading.postValue(false)
                }
            })
        } catch (e: Exception) {
            _serverResponse.postValue(SimpleResponse(true, "${e.message}"))
            isLoading.postValue(false)
        }


    }

    fun getAllOrders() {

        isLoading.postValue(true)
        val v = OrdersViewModel.repository.fetchAllOrders()
        v.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                response.body().let {
                    _allOrders.value = it
                }
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {

            }
        })
    }

    suspend fun filterTransaction(d: DateFilter) {
        isLoading.postValue(true)
        val f = NetworkService.networkInstance.filterTransaction(d)
        f.enqueue(object : Callback<List<Transaction>?> {
            override fun onResponse(
                call: Call<List<Transaction>?>,
                response: Response<List<Transaction>?>
            ) {
                //_transactions.postValue(response.body())
                //_serverResponse.postValue(SimpleResponse(true, response.body()!!.size.toString()))
                val l = response.body()
                //_transactions.postValue(response.body())
                val lst = mutableListOf<TransactionItem>()

                if(!l.isNullOrEmpty()){
                    l.forEach {
                        lst.add(TransactionItem(it))
                    }
                }

                _transactionItems.postValue(lst)
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun filterOrder(d : DateFilter){
        isLoading.postValue(true)
        val l = NetworkService.networkInstance.filterOrder(d)
        l.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                _allOrders.postValue(response.body())
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
                isLoading.postValue(false)
            }
        })
    }
}