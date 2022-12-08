package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.databinding.DeletableItemCardBinding
import java.util.zip.Inflater

class DeletableItemsAdapter(val list : List<Any>,private val deleter : (Any) -> Unit) :
    RecyclerView.Adapter<DeletableItemsAdapter.ViewHolder>() {

    class ViewHolder (binding : DeletableItemCardBinding) : RecyclerView.ViewHolder(binding.root){
        val name = binding.tvName
        val btnDelete = binding.ivDelete
        val card = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(DeletableItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setUpGenericViewHolder(holder,list[position])
    }

    override fun getItemCount() = list.size

    private fun setUpGenericViewHolder(holder: ViewHolder,item: Any){
        with(holder){
            name.text  = when(item){
                is Size -> "${item.width} x ${item.height}"
                is Material -> item.name
                is Language -> item.name
                else -> ""
            }
            btnDelete.setOnClickListener { deleter(item) }
        }
    }
}