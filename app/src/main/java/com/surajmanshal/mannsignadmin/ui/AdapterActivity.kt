package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.adapter.CategoryAdapter
import com.surajmanshal.mannsignadmin.data.model.Category

open class AdapterActivity : AppCompatActivity() {

    fun setAdapterWithList(list: List<Category>,recyclerView: RecyclerView,adapter : RecyclerView.Adapter<*>){
        if(!list.isEmpty()) recyclerView.adapter = adapter
    }
}