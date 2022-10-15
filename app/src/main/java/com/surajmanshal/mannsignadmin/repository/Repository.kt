package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository() {
    fun fetchMaterials() = NetworkService.networkInstance.fetchMaterials()

    fun fetchLanguages() = NetworkService.networkInstance.fetchLanguages()

    fun fetchSubCategories() = NetworkService.networkInstance.fetchSubCategories()
}