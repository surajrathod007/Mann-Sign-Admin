package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.databinding.ResourceItemCardBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

class SubCategoryAdapter(val vm: CategoryViewModel, editor : (Any) -> Unit) : CategoryAdapter(vm,editor) {

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val subCategory = vm.subCategories.value!![position]
        with(holder){
            binding.resourceImageView.show()
            subCategory.imgUrl?.let{
                Glide.with(binding.root).load(Functions.urlMaker(it)).into(binding.ivResource)
                binding.tvNoImage.hide()
            }
            name.text = subCategory.name
            btnDelete.setOnClickListener {
                vm.onDeleteAlert(subCategory)
            }
            btnEdit.setOnClickListener {
                editor(subCategory)
            }
        }
    }

    override fun getItemCount(): Int {
        return vm.subCategories.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ResourceItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}