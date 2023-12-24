package com.surajmanshal.mannsignadmin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsViewModel : ViewModel() {
    var isLoading = false
    private val repository = Repository()

    // -------------- LIVE DATA -------------------------------------------
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    val _currentProduct = MutableLiveData<Product>()
    val _currentProductCategory = MutableLiveData<Category>()
    val _currentProductSubCategory = MutableLiveData<SubCategory>()
    val _currentProductMaterial = MutableLiveData<Material>()
    val _currentProductLanguage = MutableLiveData<Language>()
    var quoteReq: List<String>? = null
    //    val currentProduct : LiveData<Product> get() = _currentProduct

    fun getPosters() {
        val response = repository.fetchPosters()
        response.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                response.body()?.let { _products.value = it }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                println(t.toString())
            }
        })
    }

    var loadedPage = 0
    fun getMorePosters(){
        if (loadedPage < 0) {
            return
        }
        val pageNumber : Int = loadedPage + 1
        val response = NetworkService.networkInstance.getProducts(pageNumber)
        response.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                response.body()?.let {
                    loadedPage += 1
                    if (it.isNotEmpty()) {
                        _products.value = it
                    }else{
                        loadedPage = -1
                    }
                    Log.e("GetPosters", "pageNo:$pageNumber $it")
                }
            }
            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                Log.e("GetPosters", "pageNo:$pageNumber $t")
            }
        })
    }

    fun getCategoryById(id: Int) {
        val response = repository.getCategoryById(id)
        response.enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                println(response.body())
                response.body()?.let { _currentProductCategory.value = it }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
//                TODO("Not yet implemented")
            }

        })
    }

    fun getSubCategoryById(id: Int) {
        val response = repository.getSubCategoryById(id)
        response.enqueue(object : Callback<SubCategory> {
            override fun onResponse(call: Call<SubCategory>, response: Response<SubCategory>) {
                println(response.body())
                response.body()?.let { _currentProductSubCategory.value = it }
            }

            override fun onFailure(call: Call<SubCategory>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun getMaterialById(id: Int) {
        val response = repository.getMaterialById(id)
        response.enqueue(object : Callback<Material> {
            override fun onResponse(call: Call<Material>, response: Response<Material>) {
                println(response.body())
                response.body()?.let { _currentProductMaterial.value = it }
            }

            override fun onFailure(call: Call<Material>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun getLanguageById(id: Int) {
        val response = repository.getLanguageById(id)
        response.enqueue(object : Callback<Language> {
            override fun onResponse(call: Call<Language>, response: Response<Language>) {
                println(response.body())
                response.body()?.let { _currentProductLanguage.value = it }
            }

            override fun onFailure(call: Call<Language>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    suspend fun deleteProduct(product: Product) = repository.deleteProduct(product)
    suspend fun searchProducts(query: String?): List<Product>? = NetworkService.networkInstance.queryProducts(query)

}