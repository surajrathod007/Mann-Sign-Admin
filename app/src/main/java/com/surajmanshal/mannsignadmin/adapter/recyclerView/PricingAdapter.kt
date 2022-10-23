package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.data.model.Area
import com.surajmanshal.mannsignadmin.data.model.DiscountCoupon
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.product.ProductType
import com.surajmanshal.mannsignadmin.databinding.FragmentItemBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PricingAdapter(
    val list: List<Any>,
    val vm : PricingViewModel
) : RecyclerView.Adapter<PricingAdapter.PricingViewHolder>() {

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
        val etName = EditText(holder.name.context)
        with(holder) {
            var id: Any? = null
            var changeFor: Int = -1
            when (item) {
                is Area -> {
                    name.text = "${item.area} - ${item.pinCode}"
                    id = item.pinCode
                    changeFor = Constants.CHANGE_DELIVERY_PRICE
                    holder.price.text = "₹ ${item.minCharge}"
                    etName.setText("${item.minCharge}")
                }
                is Material -> {
                    name.text = item.name
                    id = item.id!!
                    changeFor = Constants.CHANGE_MATERIAL_PRICE
                    holder.price.text = "₹ ${item.price}"
                    etName.setText("${item.price}")
                }
                is ProductType -> {
                    name.text = item.name
                    id = item.typeId!!
                    changeFor = Constants.CHANGE_BASE_PRICE
                    holder.price.text = "₹ ${item.price}"
                    etName.setText("${item.price}")
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
                    dialog.setView(etName)
                    dialog.setPositiveButton("Set", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.setNewPrice(id!!, etName.text.toString().toFloat(), changeFor)
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