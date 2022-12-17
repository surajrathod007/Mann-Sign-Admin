package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ItemProductLayoutBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.utils.Functions

class ProductsAdapter(val productList : List<Product>,val activity: ProductsActivity)  : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>(){

    class ProductsViewHolder(binding : ItemProductLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.ivProduct
        val title = binding.tvProductTitle
        val card = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(ItemProductLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = productList[position]
        with(holder){
            with(product){
                val urls = images?.let {
                    if(it.isNotEmpty())
                        return@let it.get(0).let { Functions.urlMaker(it.url) } else "TODO : Replace with placeholder"
                }
                Glide.with(image.context).load(urls).centerCrop().into(image)
                title.text = posterDetails!!.title
                card.setOnClickListener {
                    activity.replaceFragment(1,product)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}