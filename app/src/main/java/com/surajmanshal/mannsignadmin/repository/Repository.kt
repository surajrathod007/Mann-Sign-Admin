package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.network.NetworkService
import okhttp3.MultipartBody
import retrofit2.http.Multipart

class Repository() {
    fun fetchMaterials() = NetworkService.networkInstance.fetchMaterials()

    fun fetchLanguages() = NetworkService.networkInstance.fetchLanguages()

    fun fetchSubCategories() = NetworkService.networkInstance.fetchSubCategories()

    fun fetchSizes() = NetworkService.networkInstance.fetchSystemSizes()

    suspend fun sendProduct(product: Product) = NetworkService.networkInstance.sendProduct(product)

    fun fetchPosters() = NetworkService.networkInstance.fetchAllPosters()

    suspend fun uploadImage(part : MultipartBody.Part) = NetworkService.networkInstance.uploadImage(part)
}