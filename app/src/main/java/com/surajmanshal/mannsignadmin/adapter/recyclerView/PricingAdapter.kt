package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.data.model.Area
import com.surajmanshal.mannsignadmin.data.model.DiscountCoupon
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.product.ProductType
import com.surajmanshal.mannsignadmin.databinding.FragmentItemBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.Functions
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PricingAdapter(
    val list: List<Any>,
    val vm : PricingViewModel
) : RecyclerView.Adapter<PricingAdapter.PricingViewHolder>() {
    private val x = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,)
    init { x.setMargins(8,0,8,0)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PricingViewHolder {

        return PricingViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PricingViewHolder, position: Int) {
        println(list)
        val item = list[position]
        var editTextValue = ""
        with(holder) {
            var id: Any? = null
            var changeFor: Int = -1
            when (item) {
                is Area -> {
                    name.text = "${item.area} - ${item.pinCode}"
                    id = item.pinCode
                    changeFor = Constants.CHANGE_DELIVERY_PRICE
                    holder.price.text = "₹ ${item.minCharge}"
                    editTextValue = "${item.minCharge}"
                }
                is Material -> {
                    name.text = item.name
                    id = item.id!!
                    changeFor = Constants.CHANGE_MATERIAL_PRICE
                    holder.price.text = "₹ ${item.price}"
                    editTextValue ="${item.price}"
                }
                is ProductType -> {
                    name.text = item.name
                    id = item.typeId!!
                    changeFor = Constants.CHANGE_BASE_PRICE
                    holder.price.text = "₹ ${item.price}"
                    editTextValue ="${item.price}"
                }
                is DiscountCoupon -> {
                    name.text = "Coupon Code: \"${item.couponCode}\"\nRemaining: ${item.qty}"
                    holder.price.text = "${item.value} %"
                }
            }
            println(item)

            if (item !is DiscountCoupon) {
                card.setOnClickListener {
                    val dialog = android.app.AlertDialog.Builder(it.context)
                    dialog.setTitle("New Price")
                    val dialogContentLayout = LinearLayout(it.context)
                    dialogContentLayout.setPadding(32,0,32,0)
                    val etPrice = EditText(holder.name.context)
                    Functions.setTypeNumber(etPrice)
                    etPrice.setText(editTextValue)
                    dialogContentLayout.addView(etPrice)
                    dialog.setView(dialogContentLayout)
                    etPrice.layoutParams = x
                    dialog.setPositiveButton("Set", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.setNewPrice(id!!, etPrice.text.toString().toFloat(), changeFor)
                                when (item) {
                                    is ProductType -> vm.getProductTypes()
                                    is Material -> vm.getMaterials()
                                    is Area -> vm.getAreas()
                                }
                            }
                        }
                    })
                    dialog.show()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class PricingViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvProductType
        val price = binding.tvBasePrice
        val card = binding.root
    }

}