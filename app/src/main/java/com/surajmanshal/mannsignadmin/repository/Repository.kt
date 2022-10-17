package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.network.NetworkService
import okhttp3.MultipartBody
import retrofit2.Call
import java.io.File

class Repository() {
    private val server = NetworkService.networkInstance
    fun fetchMaterials() = server.fetchMaterials()

    fun fetchLanguages() = server.fetchLanguages()

    fun fetchSubCategories() = server.fetchSubCategories()

    fun fetchSizes() = server.fetchSystemSizes()

    fun fetchAllOrders() = server.fetchAllOrders()

    suspend fun sendProduct(product: Product) = server.sendProduct(product)

    fun fetchPosters() = server.fetchAllPosters()

    suspend fun uploadImage(part : MultipartBody.Part) = server.uploadImage(part)

    fun fetchImage(url:String): Call<File> {
        val filename = url.substringAfter("src/storage/images/")
        println(filename)
        return server.fetchImageByName(filename)
    }
}