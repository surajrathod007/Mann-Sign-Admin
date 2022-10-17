package com.surajmanshal.mannsignadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

class CategoryAdapter(val vm: CategoryViewModel) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(binding: ActivityCategoryManagementBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemCount(): Int {
        return if( vm.categories.value!=null) vm.categories.value!!.size
        else 0
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
//        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ActivityCategoryManagementBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}