package com.surajmanshal.mannsignadmin.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.ui.OrderDetailsActivity
import com.surajmanshal.mannsignadmin.utils.Constants

class OrdersAdapter(val context: Context, val orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderTitle = itemView.findViewById<TextView>(R.id.txtOrderId)
        val txtOrderDate = itemView.findViewById<TextView>(R.id.txtOrderDate)
        val txtOrderEmailId = itemView.findViewById<TextView>(R.id.txtOrderEmailId)
        val txtOrderTotal = itemView.findViewById<TextView>(R.id.txtOrderTotal)
        val txtOrderStatus = itemView.findViewById<TextView>(R.id.txtOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.txtOrderTitle.text = order.orderId
        holder.txtOrderDate.text = order.orderDate
        holder.txtOrderEmailId.text = order.emailId
        holder.txtOrderTotal.text = "$ ${order.total}"

        holder.itemView.setOnClickListener {
            val i = Intent(it.context,OrderDetailsActivity::class.java)
            i.putExtra("status",order.orderStatus)
            it.context.startActivity(i,Bundle())
        }

        when (order.orderStatus) {
            Constants.ORDER_PENDING -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Pending"
            }
            Constants.ORDER_CONFIRMED -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Confirmed"
            }
            Constants.ORDER_PROCCESSING -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Processing"
            }
            Constants.ORDER_READY -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Ready"
            }
            Constants.ORDER_DELIVERED -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Delivered"
            }
            Constants.ORDER_CANCELED -> {
                holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                holder.txtOrderStatus.text = "Canceled"
            }
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

}