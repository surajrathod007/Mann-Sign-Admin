package com.surajmanshal.mannsignadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.databinding.ItemCategoryCardBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

class CategoryAdapter(val vm: CategoryViewModel) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    val list = vm.categories.value?: listOf()
    class CategoryViewHolder(binding: ItemCategoryCardBinding):RecyclerView.ViewHolder(binding.root){
        val name = binding.tvCategory
        val btnDelete = binding.ivDelete
    }

    override fun getItemCount(): Int {
        /*return if( vm.categories.value!=null) vm.categories.value!!.size
        else 0*/
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val c = list[position]
        with(holder){
            name.text = c.name
            btnDelete.setOnClickListener {
                Toast.makeText(it.context, "Clicked Delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemCategoryCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}