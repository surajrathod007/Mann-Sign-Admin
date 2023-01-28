package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.ordering.OrderItem
import com.surajmanshal.mannsignadmin.ui.activity.OrderItemDetailsActivity
import com.surajmanshal.mannsignadmin.utils.Functions

class OrderItemsAdapter(val context: Context, val orderItems: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderItemTitle = itemView.findViewById<TextView>(R.id.txtOrderItemTitle)
        val txtOrderItemBasePrice = itemView.findViewById<TextView>(R.id.txtOrderItemBasePrice)
        val txtOrderItemQty = itemView.findViewById<TextView>(R.id.txtOrderItemQty)
        val txtOrderItemTotalPrice = itemView.findViewById<TextView>(R.id.txtOrderItemTotalPrice)
        val imgProduct = itemView.findViewById<ImageView>(R.id.imgProduct)
        val txtProductType = itemView.findViewById<TextView>(R.id.txtProductType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.order_product_items_layout, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val o = orderItems[position]
        with(holder) {
            //txtOrderItemTitle.text = o.product!!.productId.toString()       //here we check condition either it is poster,acp or banner
            txtOrderItemQty.text = "Quantity : " + o.quantity.toString()
            txtOrderItemBasePrice.text = "Base Price : ₹" + o.product!!.basePrice.toString()
            txtOrderItemTotalPrice.text = "₹" + o.totalPrice.toString()
            with(o) {
                if (!product?.images.isNullOrEmpty()) {
                    Glide.with(context)
                        .load(Uri.parse(Functions.urlMaker(product?.images?.get(0)?.url.toString())))
                        .placeholder(
                            R.drawable.no_photo
                        )
                        .into(imgProduct)
                }else{
                    Functions.makeToast(context, "No image url !")
                }
            }
            if (o.product!!.posterDetails != null) {
                txtOrderItemTitle.text = o.product!!.posterDetails!!.title.toString()
                txtProductType.text = "Poster"
            }
            if (o.product!!.boardDetails != null) {
                txtProductType.text = "ACP Board"
            }
            if (o.product!!.bannerDetails != null) {
                txtProductType.text = "Banner"
            }

            itemView.setOnClickListener {

                val i = Intent(it.context, OrderItemDetailsActivity::class.java)
                i.putExtra("orderItem",o)
                it.context.startActivity(i)

            }
        }
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }
}