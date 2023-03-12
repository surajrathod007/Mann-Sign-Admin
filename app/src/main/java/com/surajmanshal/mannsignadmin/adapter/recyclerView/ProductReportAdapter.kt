package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ItemProductReportLayoutBinding
import com.surajmanshal.mannsignadmin.utils.Functions

class ProductReportAdapter(val lst: List<Product>, val c: Context) :
    RecyclerView.Adapter<ProductReportAdapter.ProductReportViewHolder>() {

    class ProductReportViewHolder(val binding: ItemProductReportLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProductReport
        val title = binding.txtProductTitleReport
        val category = binding.txtProductCategoryReport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductReportViewHolder {
        return ProductReportViewHolder(
            ItemProductReportLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductReportViewHolder, position: Int) {
        val p = lst[position]
        with(holder){

            val urls = p.images?.let {
                if(it.isNotEmpty())
                    return@let it.get(0).let { Functions.urlMaker(it.url) } else "TODO : Replace with placeholder"
            }
            Glide.with(c).load(urls).centerCrop().into(imgProduct)
            if (p.posterDetails != null) {
                title.text = p.posterDetails!!.title.toString()
                category.text = "Poster"
            }
            if (p.boardDetails != null) {
                category.text = "ACP Board"
            }
            if (p.bannerDetails != null) {
                category.text = "Banner"
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size
    }
}