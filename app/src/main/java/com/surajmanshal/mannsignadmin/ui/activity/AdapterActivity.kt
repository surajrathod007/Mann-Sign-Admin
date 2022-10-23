package com.surajmanshal.mannsignadmin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.data.model.Category

open class AdapterActivity : AppCompatActivity() {

    fun setAdapterWithList(list: List<Category>,recyclerView: RecyclerView,adapter : RecyclerView.Adapter<*>){
        if(!list.isEmpty()) recyclerView.adapter = adapter
    }
}