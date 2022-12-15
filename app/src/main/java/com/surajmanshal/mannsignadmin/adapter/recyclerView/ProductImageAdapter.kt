package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.ImageLanguage
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.databinding.ItemProductImageviewBinding
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel

class ProductImageAdapter(val vm : ProductManagementViewModel,val chooseImage : () -> Unit) :
    RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {
    val list = vm.productImages.value!!
   /* init { params.setMargins(8,0,8,0)
    }*/

    class ViewHolder (binding : ItemProductImageviewBinding) : RecyclerView.ViewHolder(binding.root){
        val ivProduct = binding.ivProduct
        val tvLanguage = binding.tvPosterLanguage
        val ivRemove = binding.ivRemove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemProductImageviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position]
        val context = holder.itemView.context
       with(holder){
           image.let{
               if(image.fileUri==null) ivProduct.setOnClickListener { chooseImage() }
               else {
                   Glide.with(context).load(image.fileUri).into(ivProduct)
                   ivRemove.apply {
                       setOnClickListener{
                           vm.removeImage(image)
                       }
                       show()
                   }
                   tvLanguage.apply {
                       text = image.languageId?.let{ it1 -> vm.languages.value?.find { it.id== it1 }?.name
                       } ?: ""
                       show()
                   }
               }
           }
       }
    }

    override fun getItemCount() = list.size

}