package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.response.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ResourcesViewModel : ViewModel() {

    protected val repository = Repository()
    protected val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse
    protected val _sizes = MutableLiveData<List<Size>>()
    val sizes : LiveData<List<Size>> get() = _sizes                                        //SIZE
    protected val _materials = MutableLiveData<List<Material>>()
    val materials : LiveData<List<Material>> get() = _materials                         //MATERIALS
    protected val _languages = MutableLiveData<List<Language>>()
    val languages  : LiveData<List<Language>> get() = _languages                        // LANGUAGES
    protected val _deletionResponse = MutableLiveData<SimpleResponse>()
    val deletionResponse : LiveData<SimpleResponse> get() = _deletionResponse
    protected val _updateResponse = MutableLiveData<SimpleResponse>()
    val updateResponse : LiveData<SimpleResponse> get() = _updateResponse
    val resourceMode =  MutableLiveData<Any>()


    suspend fun sendFont(part: MultipartBody.Part){
        try {
            val response = repository.uploadFontFile(part)
            _serverResponse.postValue(response)

        }catch (e : Exception){
            println("$e ${serverResponse.value?.message}")
        }
    }

    suspend fun sendSize(size : Size){
        val response = repository.insertSize(size)
        _serverResponse.postValue(response)
    }

    suspend fun sendMaterial(material : Material){
        val response = repository.insertMaterial(material)
        _serverResponse.postValue(response)
    }

    suspend fun sendLanguage(language : Language){
        val response = repository.insertLanguage(language)
        _serverResponse.postValue(response)
    }

    fun getSizes(){
        val response = repository.fetchSizes()
        println("Response is $response")
        response.enqueue(object : Callback<List<Size>> {
            override fun onResponse(call: Call<List<Size>>, response: Response<List<Size>>) {
                println("Inner Response is $response")
                response.body()?.let { sizes -> _sizes.value = sizes.sortedBy { it.width } }
            }
            override fun onFailure(call: Call<List<Size>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    fun getMaterials(productTypeIds: List<Int>){
        val response = repository.fetchMaterials(productTypeIds)
        println("Response is $response")
        response.enqueue(object : Callback<List<Material>> {
            override fun onResponse(call: Call<List<Material>>, response: Response<List<Material>>) {
                println("Inner Response is $response")
                response.body()?.let { materials -> _materials.value = materials.sortedBy { it.name } }
            }
            override fun onFailure(call: Call<List<Material>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    fun getLanguages(){
        val response = repository.fetchLanguages()
        println("Response is $response")
        response.enqueue(object : Callback<List<Language>> {
            override fun onResponse(call: Call<List<Language>>, response: Response<List<Language>>) {
                println("Inner Response is $response")
                response.body()?.let { _languages.value = it }
            }
            override fun onFailure(call: Call<List<Language>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun deleteResource(res: Any) {
        setResourceMode(res)
        when(res){
            is Size -> deleteSize(res.sid)
            is Material  -> deleteMaterial(res.id!!)
            is Language -> deleteLanguage(res.id)
        }

    }
    suspend fun updateResource(res: Any) {
        setResourceMode(res)
        when(res){
            is Size -> updateSize(res)
            is Material  -> updateMaterial(res)
            is Language -> updateLanguage(res)
        }
    }

    protected suspend fun deleteSize(sizeId : Int) = _deletionResponse.postValue(repository.deleteSize(sizeId))

    protected suspend fun deleteMaterial(materialId : Int) = _deletionResponse.postValue(repository.deleteMaterial(materialId))

    protected suspend fun deleteLanguage(languageId : Int) = _deletionResponse.postValue(repository.deleteLanguage(languageId))
    protected suspend fun updateLanguage(language : Language) = _updateResponse.postValue(repository.updateLanguage(language))
    protected suspend fun updateMaterial(material: Material) = _updateResponse.postValue(repository.updateMaterial(material))
    protected suspend fun updateSize(size: Size) = _updateResponse.postValue(repository.updateSize(size))

    fun setResourceMode(mode : Any) = resourceMode.postValue(mode)

}