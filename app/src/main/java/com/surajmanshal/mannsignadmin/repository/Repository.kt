package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.network.NetworkService

class Repository() {
    fun fetchMaterials() = NetworkService.networkInstance.fetchMaterials()

    fun fetchLanguages() = NetworkService.networkInstance.fetchLanguages()

    fun fetchSubCategories() = NetworkService.networkInstance.fetchSubCategories()

    fun fetchSizes() = NetworkService.networkInstance.fetchSystemSizes()

    suspend fun sendProduct(product: Product) = NetworkService.networkInstance.sendProduct(product)

    fun fetchPosters() = NetworkService.networkInstance.fetchAllPosters()
}