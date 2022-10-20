package com.surajmanshal.mannsignadmin.ui.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.surajmanshal.mannsignadmin.data.model.ProductType
import com.surajmanshal.mannsignadmin.databinding.FragmentItemBinding

class ProductPricingAdapter(
    private val list: List<ProductType>
) : RecyclerView.Adapter<ProductPricingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder){
            with(item){
                type.text = name
                price.text = "₹ $basePrice"
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val type = binding.tvProductType
        val price = binding.tvBasePrice
    }
}