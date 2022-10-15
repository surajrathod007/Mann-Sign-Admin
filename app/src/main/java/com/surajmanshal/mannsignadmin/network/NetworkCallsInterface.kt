package com.surajmanshal.mannsignadmin.network

import androidx.room.Query
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface NetworkCallsInterface {

    @GET("materials")
    fun fetchMaterials() : Call<List<Material>>

    @GET("languages")
    fun fetchLanguages() : Call<List<Language>>

    @GET("subcategories")
    fun fetchSubCategories() : Call<List<SubCategory>>

    @GET("sizes")
    fun fetchSystemSizes() : Call<List<Size>>

    @POST("product/add")
    fun sendProduct(product: Product) : SimpleResponse
}
