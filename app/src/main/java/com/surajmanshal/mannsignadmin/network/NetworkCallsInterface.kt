package com.surajmanshal.mannsignadmin.network

import androidx.room.Query
import com.surajmanshal.mannsignadmin.data.model.Material
import retrofit2.Call
import retrofit2.http.GET

interface NetworkCallsInterface {

    @GET("material/getAll")
    fun fetchMaterials() : Call<List<Material>>
}
