package com.surajmanshal.mannsignadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.databinding.ItemProductLayoutBinding
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel.Companion.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductsAdapter(val productList : List<Product>,val imagesFiles : List<File>)  : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>(){

    class ProductsViewHolder(binding : ItemProductLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.ivProduct
        val title = binding.tvProductTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(ItemProductLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = productList[position]
        with(holder){
            with(product){
//                Glide.with(image.context).load(imagesFiles[position]).into(image)
                title.text = posterDetails!!.title
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}