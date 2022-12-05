package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import com.surajmanshal.mannsignadmin.databinding.DeletableItemCardBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

class SubCategoryAdapter(val vm: CategoryViewModel) : CategoryAdapter(vm) {

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val s = vm.subCategories.value!![position]
        with(holder){
            name.text = s.name
            btnDelete.setOnClickListener {
                vm.onDeleteAlert(s)
            }
        }
    }

    override fun getItemCount(): Int {
        return vm.subCategories.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(DeletableItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}