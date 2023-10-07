package com.surajmanshal.mannsignadmin.network

import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.data.model.auth.LoginRequest
import com.surajmanshal.mannsignadmin.data.model.auth.LoginResponse
import com.surajmanshal.mannsignadmin.data.model.auth.User
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.data.model.ordering.Transaction
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.data.model.product.ProductType
import com.surajmanshal.mannsignadmin.paging.PagedOrders
import com.surajmanshal.response.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkCallsInterface {


    //User Auth
    @Headers("Content-Type: application/json")
    @POST("user/register")
    suspend fun registerUser(@Body user : User) : SimpleResponse

    @Headers("Content-Type: application/json")
    @POST("user/loginAsAdmin")
    suspend fun loginAdmin(@Body loginReq: LoginRequest) : LoginResponse

    @Headers("Content-Type: application/json")
    @POST("user/otp")
    suspend fun sendOtp(@Query("email") email : String) : SimpleResponse

    @Headers("Content-Type: application/json")
    @POST("user/resetpassword")
    suspend fun resetPassword(@Query("email") email: String, @Query("newpas") newpas : String) : SimpleResponse

    @POST("materials")
    fun fetchMaterials(@Body productTypeId: List<Int>): Call<List<Material>>

    @GET("languages")
    fun fetchLanguages() : Call<List<Language>>

    @GET("subcategories")
    fun fetchSubCategories() : Call<List<SubCategory>>

    @GET("categories")
    fun fetchCategories() : Call<List<Category>>

    @GET("sizes")
    fun fetchSystemSizes() : Call<List<Size>>

    @Headers("Content-Type: application/json")
    @POST("product/insert")
    fun sendProduct(@Body product: Product) : Call<Variant>

    @POST("product/update")
    suspend fun updateProduct(@Body product: Product) : SimpleResponse

    @GET("product/posters")
    fun fetchAllPosters() : Call<List<Product>>

    @GET("order/getall")
    fun fetchAllOrders() : Call<List<Order>>

    @Headers("Content-Type: application/json")
    @POST("order/update")
    fun updateOrder(@Body order: Order) : Call<SimpleResponse>

    @Multipart
    @POST("productImage/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part, @Query("languageId")languageId: Int) : SimpleResponse

    @GET("size")
    fun fetchSizeById(@Query("id") id:Int) : Call<Size>

    @GET("material")
    fun fetchMaterialById(@Query("id") id:Int) : Call<Material>

    @GET("language")
    fun fetchLanguageById(@Query("id") id:Int) : Call<Language>

    @POST("category/delete")
    suspend fun deleteCategory(@Query("id") id:Int) : SimpleResponse

    @POST("category/insert")
    suspend fun insertCategory(@Body category: Category): SimpleResponse

    @POST("subCategory/remove")
    suspend fun deleteSubCategory(@Query("id") id: Int): SimpleResponse

    @POST("subCategory/add")
    suspend fun insertSubCategory(@Body category: SubCategory): SimpleResponse


    @GET("category/subcategories")
    fun fetchSubCategoriesOfCategory(@Query("id") id : Int) : Call<List<SubCategory>>

    @GET("user/get")
    fun fetchUserByEmail(@Query("email") email : String) : Call<User>

    @GET("productTypes")
    fun fetchProductTypes(): Call<List<ProductType>>

    @POST("pricing/update")
    suspend fun updatePrice(@Query("id") typeId: Any, @Query("price") newPrice: Float, @Query("changeFor") changeFor: Int): SimpleResponse

    @GET("area/getAll")
    fun fetchAreas(): Call<List<Area>>

    @GET("transaction/getall")   //not added in repo
    fun fetchAllTransactions() : Call<List<Transaction>>

    @POST("transaction/filter")     //not added in repo
    fun filterTransaction(@Body dateFilter : DateFilter) : Call<List<Transaction>>

    @POST("order/filter")         //not addded in repo
    fun filterOrder(@Body dateFilter: DateFilter) : Call<List<Order>>

    @GET("discountCoupons")
    fun fetchCoupons(): Call<List<DiscountCoupon>>

    @POST("discount/add")
    suspend fun insertCoupon(@Query("code")couponCode: String,@Query("value") value: Int,@Query("qty") qty: Int): SimpleResponse

    @GET("user/getall")
    fun fetchAllUsers() : Call<List<User>>

    @POST("review/add")
    fun addReview(@Body review : Review) : Call<SimpleResponse>

    @POST("review/get")
    fun getReview(@Query("productId") productId : String) : Call<List<Review>>

    @POST("review/getUserReview")
    fun getUserReview(@Query("productId") productId : String, @Query("emailId") emailId : String) : Call<Review?>

    @POST("review/delete")
    fun deleteReview(@Query("reviewId") reviewId : String) : Call<SimpleResponse>

    @GET("category")
    fun fetchCategoryById(@Query("id") id: Int): Call<Category>

    @GET("subCategory")
    fun fetchSubCategoryById(@Query("id")id: Int): Call<SubCategory>

    @Multipart
    @POST("font/insert")
    suspend fun uploadFontFile(@Part part: MultipartBody.Part):SimpleResponse

    @POST("size/addByAdmin")
    suspend fun insertSizeByAdmin(@Body size: Size): SimpleResponse

    @POST("material/add")
    suspend fun insertMaterial(@Body material: Material): SimpleResponse

    @POST("language/add")
    suspend fun insertLanguage(@Body language: Language): SimpleResponse

    @POST("size/deleteByAdmin")
    suspend fun deleteSize(@Query("id")sizeId: Int): SimpleResponse

    @POST("material/remove")
    suspend fun deleteMaterial(@Query("id")materialId: Int): SimpleResponse

    @POST("language/remove")
    suspend fun deleteLanguage(@Query("id")languageId: Int): SimpleResponse

    @POST("area/add")
    suspend fun insertArea(@Body area: Area): SimpleResponse

    @GET("chat/getByOrderId")
    fun loadChats(@Query("orderId") orderId : String) : Call<List<ChatMessage>>

    @POST("chat/add")
    fun addChat(@Body msg : ChatMessage) : Call<SimpleResponse>

    @Multipart
    @POST("chat/uploadImage")
    suspend fun uploadChatImage(@Part image: MultipartBody.Part) : SimpleResponse

    @POST("chat/add")
    suspend fun addImageChat(@Body msg : ChatMessage) : SimpleResponse

    @POST("order/id")
    fun getOrderById(@Query("id") id : String) : Call<Order>

    @POST("size/update")
    suspend fun updateSize(@Body size : Size) : SimpleResponse
    @POST("material/update")
    suspend fun updateMaterial(@Body material : Material) : SimpleResponse
    @POST("language/update")
    suspend fun updateLanguage(@Body language : Language) : SimpleResponse
    @POST("category/update")
    suspend fun updateCategory(@Body category: Category): SimpleResponse
    @POST("subcategory/update")
    suspend fun updateSubcategory(@Body category: SubCategory): SimpleResponse

    @GET("order/all")
    suspend fun getPaginatedOrders(@Query("page") page : Int) : PagedOrders

    @GET("order/count")
    fun getOrderCount() : Call<List<Int>>

    @POST("order/status")
    fun getOrdersByStatus(@Query("status") status : String) : Call<List<Order>>

    @Multipart
    @POST("categoryImage/upload")
    suspend fun uploadCategoryImage(@Part image: MultipartBody.Part) : SimpleResponse
    @Multipart
    @POST("subcategoryImage/upload")
    suspend fun uploadSubCategoryImage(@Part image: MultipartBody.Part) : SimpleResponse

    @GET("banner/getall")
    fun getAllBanners() : Call<List<AdBanner>>

    @Multipart
    @POST("banner/add")
    suspend fun addBanner(@Part imageFile: MultipartBody.Part): SimpleResponse

    @POST("banner/update")
    fun updateBanner(@Body banner: AdBanner): Call<SimpleResponse>  // Todo : Have some conflicts

    @POST("banner/delete")
    fun deleteBanner(@Body banner: AdBanner): Call<SimpleResponse>
    @POST("product/delete")
    suspend fun deleteProduct(@Body product: Product): SimpleResponse
}
