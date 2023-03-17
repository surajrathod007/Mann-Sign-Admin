package com.surajmanshal.mannsignadmin.repository

import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.network.NetworkService
import okhttp3.MultipartBody

open class Repository() {
    private val server = NetworkService.networkInstance
    fun fetchMaterials(productTypeId: List<Int>) = server.fetchMaterials(productTypeId)

    fun fetchLanguages() = server.fetchLanguages()

    fun fetchSubCategories() = server.fetchSubCategories()

    fun fetchCategory() = server.fetchCategories()

    fun fetchSubcategories(id: Int) = server.fetchSubCategoriesOfCategory(id)

    fun fetchSizes() = server.fetchSystemSizes()

    fun fetchAllOrders() = server.fetchAllOrders()

    fun getAreas() = server.fetchAreas()

    suspend fun updateOrder(order: Order) = server.updateOrder(order)

    suspend fun sendProduct(product: Product) = server.sendProduct(product)

    suspend fun updateProduct(product: Product) = server.updateProduct(product)

    fun fetchPosters() = server.fetchAllPosters()

    suspend fun uploadImage(part: MultipartBody.Part, languageId: Int) = server.uploadImage(part,languageId)

    suspend fun deleteCategory(id: Int) = server.deleteCategory(id)

    suspend fun insertCategory(category: Category) = server.insertCategory(category)

    suspend fun deleteSubCategory(id: Int) = server.deleteSubCategory(id)

    suspend fun fetchUserByEmail(email: String) = server.fetchUserByEmail(email)

    fun fetchProductTypes() = server.fetchProductTypes()

    suspend fun updatePrice(typeId: Any, newPrice: Float, changeFor: Int) = server.updatePrice(typeId, newPrice, changeFor)

    fun getCoupons() = server.fetchCoupons()

    suspend fun insertCoupon(coupon: DiscountCoupon) = server.insertCoupon(coupon.couponCode,coupon.value,coupon.qty)

    suspend fun insertSubCategory(category: SubCategory) = server.insertSubCategory(category)

    fun getCategoryById(id: Int) = server.fetchCategoryById(id)

    fun getSubCategoryById(id: Int) = server.fetchSubCategoryById(id)

    fun getMaterialById(id:Int) =  server.fetchMaterialById(id)

    fun getLanguageById(id:Int) =  server.fetchLanguageById(id)

    suspend fun uploadFontFile(part: MultipartBody.Part) = server.uploadFontFile(part)

    suspend fun insertSize(size: Size) = server.insertSizeByAdmin(size)

    suspend fun insertMaterial(material: Material) = server.insertMaterial(material)

    suspend fun insertLanguage(language : Language) = server.insertLanguage(language)

    suspend fun deleteSize(sizeId: Int) = server.deleteSize(sizeId)

    suspend fun deleteMaterial(materialId: Int) = server.deleteMaterial(materialId)

    suspend fun deleteLanguage(languageId: Int) = server.deleteLanguage(languageId)

    suspend fun insertArea(area: Area) = server.insertArea(area)

    suspend fun updateSize(size : Size) = server.updateSize(size)

    suspend fun updateMaterial(material : Material) = server.updateMaterial(material)

    suspend fun updateLanguage(language : Language) = server.updateLanguage(language)
}