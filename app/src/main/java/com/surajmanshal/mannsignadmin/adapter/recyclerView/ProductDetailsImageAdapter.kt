package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.ImageLanguage
import com.surajmanshal.mannsignadmin.databinding.ItemProductImageviewBinding
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.utils.show

class ProductDetailsImageAdapter(val images : List<Pair<String,String>>) : RecyclerView.Adapter<ProductDetailsImageAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemProductImageviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivProduct = binding.ivProduct
        val tvLanguage = binding.tvPosterLanguage
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemProductImageviewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        val context = holder.itemView.context
        with(holder){
            Glide.with(context).load(Functions.urlMaker(image.first)).into(ivProduct)
            tvLanguage.apply {
                text = image.second
                show()
            }
        }
    }
}