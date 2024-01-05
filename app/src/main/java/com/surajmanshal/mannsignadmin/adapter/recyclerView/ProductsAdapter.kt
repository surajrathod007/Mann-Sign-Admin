package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ItemProductLayoutBinding
import com.surajmanshal.mannsignadmin.ui.activity.ProductsActivity
import com.surajmanshal.mannsignadmin.utils.Functions

class ProductsAdapter( val activity: ProductsActivity)  : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>(){

    var productList : MutableList<Product> = mutableListOf()

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
                    activity.replaceFragment(1, product)
                }
            }
        }
    }

    fun submitData(products : List<Product>){
        val oldSize = productList.size
        productList.addAll(products)
        when(products.size){
            0 -> {}
            1 -> { notifyItemInserted(oldSize + 1) }
            else -> { notifyItemRangeInserted(oldSize,products.size)  }
        }
        println(productList.size)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun changeDate(resultList: List<Product>) {
        val oldSize = productList.size
        productList.clear()
        notifyItemRangeRemoved(0,oldSize)
        productList.addAll(resultList)
        notifyItemRangeInserted(0,resultList.size)
    }

    fun clearProducts() {
        val size = productList.size
        productList.clear()
        notifyItemRangeRemoved(0,size)
    }

}