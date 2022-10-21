package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.ProductType
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PricingViewModel : ViewModel(),java.io.Serializable{
    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _productTypes = MutableLiveData<List<ProductType>>(mutableListOf())
    val productTypes: LiveData<List<ProductType>> get() = _productTypes
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE


    fun getProductTypes(){
        val response = repository.fetchProductTypes()
        response.enqueue(object : Callback<List<ProductType>> {
            override fun onResponse(
                call: Call<List<ProductType>>,
                response: Response<List<ProductType>>
            ) {
                print(response.body().toString())
                response.body()?.let {
                    _productTypes.value = it
                }
            }
            override fun onFailure(call: Call<List<ProductType>>, t: Throwable) {
                //TODO("Not yet implemented")
            }
        })
    }

    suspend fun setNewPrice(typeId: Int, newPrice: Float, changeFor: Int) {
        try {
            val response = repository.updatePrice(typeId,newPrice,changeFor)
            _serverResponse.postValue(response)
        }catch (e : Exception){
            println("$e")
        }
    }
}