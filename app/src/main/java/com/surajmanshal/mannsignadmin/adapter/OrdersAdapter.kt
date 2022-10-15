package com.surajmanshal.mannsignadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Order

class OrdersAdapter(val context : Context,val orders : List<Order>)  : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>(){

    class OrderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val txtOrderTitle = itemView.findViewById<TextView>(R.id.txtOrderId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_item_layout,parent,false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.txtOrderTitle.text = order.orderId
    }

    override fun getItemCount(): Int {
        return orders.size
    }

}