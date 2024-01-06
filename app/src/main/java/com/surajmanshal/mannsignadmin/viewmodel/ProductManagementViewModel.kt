package com.surajmanshal.mannsignadmin.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surajmanshal.mannsignadmin.data.model.ImageLanguage
import com.surajmanshal.mannsignadmin.data.model.SubCategory
import com.surajmanshal.mannsignadmin.data.model.Variant
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody.Part
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementViewModel : ResourcesViewModel() {

    // -------------- LIVE DATA -------------------------------------------
    private val _subCategories = MutableLiveData<List<SubCategory>>()
    val subCategories: LiveData<List<SubCategory>> get() = _subCategories               //CATEGORIES
    private val _imageUploadResponse = MutableLiveData<SimpleResponse>()
    val imageUploadResponse: LiveData<SimpleResponse> get() = _imageUploadResponse     // IMAGE UPLOADING PROGRESS
    private val _productUploadResponse = MutableLiveData<Pair<Variant, String>>()
    val productUploadResponse: LiveData<Pair<Variant, String>> get() = _productUploadResponse  // PRODUCT UPLOADING PROGRESS

     private val _productUpdateResponse = MutableLiveData<SimpleResponse>()
    val productUpdateResponse: LiveData<SimpleResponse> get() = _productUpdateResponse  // PRODUCT UPLOADING PROGRESS
    private val _posters = MutableLiveData<List<Product>>()
    val posters: LiveData<List<Product>> get() = _posters                              // POSTERS
    private val _posterCodes = MutableLiveData<List<String>>()
    val posterCodes: LiveData<List<String>> get() = _posterCodes                              // POSTERS

     val _productImages =
        MutableLiveData<MutableList<ImageLanguage>>(mutableListOf(ImageLanguage()))
    val productImages: LiveData<MutableList<ImageLanguage>> get() = _productImages                 // PRODUCT IMAGES
    var activeImage : ImageLanguage? = null

    // -------------- DATA SETUP FUNCTIONS -------------------------------------------
    suspend fun setupViewModelDataMembers() {
        CoroutineScope(Dispatchers.IO).launch { getSizes() }
        CoroutineScope(Dispatchers.IO).launch { getMaterials(listOf(Constants.TYPE_POSTER)) }
        CoroutineScope(Dispatchers.IO).launch { getLanguages() }
        CoroutineScope(Dispatchers.IO).launch { getSubCategories() }
//        CoroutineScope(Dispatchers.IO).launch { getPosters() }
        CoroutineScope(Dispatchers.IO).launch { getPosterCodes() }
    }


    private suspend fun getSubCategories() {
        val response = repository.fetchSubCategories()
        println("Response is $response")
        response.enqueue(object : Callback<List<SubCategory>> {
            override fun onResponse(
                call: Call<List<SubCategory>>,
                response: Response<List<SubCategory>>
            ) {
                println("Inner Response is $response")
                response.body()?.let { _subCategories.value = it }
            }

            override fun onFailure(call: Call<List<SubCategory>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun addProduct(product: Product) {
        try {
            repository.sendProduct(product).enqueue(object : Callback<Variant?> {

                override fun onResponse(call: Call<Variant?>, response: Response<Variant?>) {
                    val body = response.body()
                    if (body == null){
                        _productUploadResponse.postValue(Pair(Variant(null, -9,
                            0,
                            0,
                            0,0.0f
                        ), "Null variant returned"))
                    }else{
                        _productUploadResponse.postValue(Pair(body, "Uploaded"))
                    }
                }

                override fun onFailure(call: Call<Variant?>, t: Throwable) {
                    println("$t")
                    _productUploadResponse.postValue(Pair(Variant(null, -9,
                        0,
                        0,
                        0,0.0f
                    ), t.toString()))
                }
            })

        } catch (e: Exception) {
            _productUploadResponse.postValue(Pair(Variant(null, -9,
                0,
                0,
                0,0.0f
            ), e.toString()))
        }
    }


    suspend fun updateProduct(product: Product) {
        try {
            val response = repository.updateProduct(product)
            _serverResponse.postValue(response)
            _productUpdateResponse.postValue(response)
        } catch (e: Exception) {
            println("$e")
        }
    }

    suspend fun getPosters() {
        val response = repository.fetchPosters()
        println("Response is $response")
        response.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                println("Inner Response is $response")
                response.body()?.let { _posters.value = it }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun getPosterCodes() {
        val response = repository.fetchPosterCodes()
        println("Response is $response")
        response.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                println("Inner Response is $response")
                response.body()?.let { _posterCodes.value = it }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun sendImage(part: Part, languageId: Int) {
        try {
            val response = repository.uploadImage(part, languageId)
            _serverResponse.postValue(response)
            _imageUploadResponse.postValue(response)
        } catch (e: Exception) {
            println("${this.javaClass.name}$e ${serverResponse.value?.message}")
        }
    }

    fun addImage(uri: Uri, languageId: Int) {
        _productImages.value?.apply {
            if (activeImage == null) { // Insertion
                add(last())
                set(lastIndexOf(last()) - 1, ImageLanguage().apply {
                    fileUri = uri
                    this.languageId = languageId
                })
            }else{ // Update
                set(lastIndexOf(activeImage), ImageLanguage().apply {
                    fileUri = uri
                    this.languageId = languageId
                })
                activeImage = null
            }
        }
        refreshProductImages()
    }

    fun removeImage(item: ImageLanguage) {
        _productImages.value?.remove(item)
        refreshProductImages()
    }

    fun refreshProductImages() = _productImages.postValue(_productImages.value)

}