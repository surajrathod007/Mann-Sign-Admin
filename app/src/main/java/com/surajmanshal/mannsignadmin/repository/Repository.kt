package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.response.SimpleResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

class Repository() {
    private val server = NetworkService.networkInstance
    fun fetchMaterials() = server.fetchMaterials()

    fun fetchLanguages() = server.fetchLanguages()

    fun fetchSubCategories() = server.fetchSubCategories()

    fun fetchCategory() = server.fetchCategories()

    fun fetchSubcategories(id:Int) = server.fetchSubCategoriesOfCategory(id)

    fun fetchSizes() = server.fetchSystemSizes()

    fun fetchAllOrders() = server.fetchAllOrders()

    suspend fun updateOrder(order: Order) = server.updateOrder(order)

    suspend fun sendProduct(product: Product) = server.sendProduct(product)

    fun fetchPosters() = server.fetchAllPosters()

    suspend fun uploadImage(part : MultipartBody.Part) = server.uploadImage(part)

    suspend fun deleteCategory(id : Int) = server.deleteCategory(id)

    suspend fun insertCategory(category: Category) = server.insertCategory(category)

    suspend fun deleteSubCategory(id: Int) = server.deleteSubCategory(id)
}