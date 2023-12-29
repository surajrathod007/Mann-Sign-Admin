package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.databinding.ItemProductImageviewBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.show
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel

class ProductImageAdapter(val vm : ProductManagementViewModel, val editMode : Boolean = false ,val chooseImage : () -> Unit) :
    RecyclerView.Adapter<ProductImageAdapter.ViewHolder>() {
    val list = vm.productImages.value!!
   /* init { params.setMargins(8,0,8,0)
    }*/

    class ViewHolder (binding : ItemProductImageviewBinding) : RecyclerView.ViewHolder(binding.root){
        val ivProduct = binding.ivProduct
        val tvLanguage = binding.tvPosterLanguage
        val ivRemove = binding.ivRemove
        val ivEdit = binding.ivEdit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemProductImageviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position]
        val context = holder.itemView.context
       with(holder){
           if(image.fileUri==null && image.imgUrl == null) {
               ivProduct.setOnClickListener { chooseImage() }
               return
           }

           if (image.fileUri != null) {
               Glide.with(context).load(image.fileUri).into(ivProduct)
           }
           if (image.imgUrl != null){
               Glide.with(context).load(Functions.urlMaker(image.imgUrl!!)).into(ivProduct)
           }
           tvLanguage.apply {
               text = image.languageId?.let{ it1 -> vm.languages.value?.find { it.id== it1 }?.name
               } ?: ""
               show()
           }
           ivRemove.apply {
               setOnClickListener{
                   vm.removeImage(image)
               }
               show()
           }
           if (editMode){
               ivEdit.apply {
                   setOnClickListener{
                       vm.activeImage = image
                       chooseImage()
                   }
                   show()
               }
           }
       }
    }

    override fun getItemCount() = list.size

}