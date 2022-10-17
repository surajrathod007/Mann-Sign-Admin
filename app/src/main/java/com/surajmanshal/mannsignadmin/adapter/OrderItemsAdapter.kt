package com.surajmanshal.mannsignadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.OrderItem

class OrderItemsAdapter(val context: Context, val orderItems: List<OrderItem>) : RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>(){

    class OrderItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val txtOrderItemTitle = itemView.findViewById<TextView>(R.id.txtOrderItemTitle)
        val txtOrderItemBasePrice = itemView.findViewById<TextView>(R.id.txtOrderItemBasePrice)
        val txtOrderItemQty = itemView.findViewById<TextView>(R.id.txtOrderItemQty)
        val txtOrderItemTotalPrice = itemView.findViewById<TextView>(R.id.txtOrderItemTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_product_items_layout, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val o = orderItems[position]
        with(holder){
            txtOrderItemTitle.text = o.product!!.productId.toString()       //here we check condition either it is poster,acp or banner
            txtOrderItemQty.text = "Quantity : "+o.quantity.toString()
            txtOrderItemBasePrice.text = "Base Price : $" + o.product!!.basePrice.toString()
            txtOrderItemTotalPrice.text = o.totalPrice.toString()
        }
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }
}