package com.surajmanshal.mannsignadmin.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.surajmanshal.mannsignadmin.data.model.ProductType
import com.surajmanshal.mannsignadmin.databinding.FragmentItemBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductPricingAdapter(
    private val products: List<ProductType>,val vm : PricingViewModel
) : RecyclerView.Adapter<ProductPricingAdapter.ViewHolder>() {
    val list : List<ProductType> = vm.productTypes.value?: listOf()
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
                card.setOnClickListener {
                    val dialog = AlertDialog.Builder(it.context)
                    dialog.setTitle("New Price")
                    val etName = EditText(it.context)
                    etName.setText("$basePrice")
                    dialog.setView(etName)
                    dialog.setPositiveButton("Set", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            // todo : Set new price
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.setNewPrice(typeId!!,etName.text.toString().toFloat(), Constants.CHANGE_BASE_PRICE)
                                vm.getProductTypes()
                            }
                        }
                    })
                    dialog.show()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val type = binding.tvProductType
        val price = binding.tvBasePrice
        val card = binding.root
    }
}