package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.ItemCategoryCardBinding
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

open class CategoryAdapter(private val vm: CategoryViewModel) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(binding: ItemCategoryCardBinding):RecyclerView.ViewHolder(binding.root){
        val name = binding.tvCategory
        val btnDelete = binding.ivDelete
        val card = binding.root
    }

    override fun getItemCount(): Int {
        /*return if( vm.categories.value!=null) vm.categories.value!!.size
        else 0*/
        return vm.categories.value!!.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val c = vm.categories.value!![position]
        println(c)
        Log.d("category","$c")
        with(holder){
            name.text = c.name
            btnDelete.setOnClickListener {
                vm.onDeleteAlert(c)
            }
            card.setOnClickListener {
                val bundle =Bundle()
                c.id?.let { it1 -> bundle.putInt("id", it1) }
                Navigation.findNavController(it).navigate(R.id.action_categoryFragment_to_subCategoryFragment,bundle)
//                Toast.makeText(it.context, "${c.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
}