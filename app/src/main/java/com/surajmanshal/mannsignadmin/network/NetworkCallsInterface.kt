package com.surajmanshal.mannsignadmin.network

import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkCallsInterface {

    @GET("order/getall")
    fun fetchAllOrders() : Call<List<Order>>

    @GET("materials")
    fun fetchMaterials() : Call<List<Material>>

    @GET("languages")
    fun fetchLanguages() : Call<List<Language>>

    @GET("subcategories")
    fun fetchSubCategories() : Call<List<SubCategory>>

    @GET("sizes")
    fun fetchSystemSizes() : Call<List<Size>>

    @Headers("Content-Type: application/json")
    @POST("product/insert")
    suspend fun sendProduct(@Body product: Product) : SimpleResponse
}
