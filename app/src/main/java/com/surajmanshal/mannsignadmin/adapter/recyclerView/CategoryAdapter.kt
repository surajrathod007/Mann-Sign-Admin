package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.ResourceItemCardBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.hide
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

open class CategoryAdapter(private val vm: CategoryViewModel , val editor : (Any) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ResourceItemCardBinding):RecyclerView.ViewHolder(binding.root){
        val name = binding.tvName
        val btnDelete = binding.ivDelete
        val btnEdit = binding.ivEdit
        val card = binding.root
    }

    override fun getItemCount(): Int {
        /*return if( vm.categories.value!=null) vm.categories.value!!.size
        else 0*/
        return vm.categories.value!!.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = vm.categories.value!![position]
        println(category)
        Log.d("category","$category")
        with(holder){
            binding.resourceImageView.show()
            category.imgUrl?.let{
                Glide.with(binding.root).load(Functions.urlMaker(it)).into(binding.ivResource)
                binding.tvNoImage.hide()
            }
            name.text = category.name
            btnDelete.setOnClickListener {
                vm.onDeleteAlert(category)
            }
            btnEdit.apply{
                show()
                setOnClickListener { editor(category) }
            }
            card.setOnClickListener {
                val bundle =Bundle()
                category.id?.let { it1 -> bundle.putInt("id", it1) }
                Navigation.findNavController(it).navigate(R.id.action_categoryFragment_to_subCategoryFragment,bundle)
//                Toast.makeText(it.context, "${c.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ResourceItemCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
}