package com.surajmanshal.mannsignadmin.paging

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.DifferCallback
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.databinding.ItemOrderLayoutBinding
import com.surajmanshal.mannsignadmin.ui.activity.OrderDetailsActivity
import com.surajmanshal.mannsignadmin.utils.Constants

class PagedOrderAdapter : PagingDataAdapter<Order, PagedOrderAdapter.OrdersViewHolder>(COMPARATOR) {

    class OrdersViewHolder(val binding: ItemOrderLayoutBinding) : ViewHolder(binding.root) {
        val txtOrderTitle = binding.txtOrderId
        val txtOrderDate = binding.txtOrderDate
        val txtOrderEmailId = binding.txtOrderEmailId
        val txtOrderTotal = binding.txtOrderTotal
        val txtOrderStatus = binding.txtOrderStatus
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = getItem(position)
        val context = holder.binding.root.context
        if (order != null) {
            holder.txtOrderTitle.text = order.orderId
            holder.txtOrderDate.text = order.orderDate.toString()
            holder.txtOrderEmailId.text = order.emailId
            holder.txtOrderTotal.text = "₹ ${order.total}"


            holder.itemView.setOnClickListener {
                val i = Intent(it.context, OrderDetailsActivity::class.java)
                i.putExtra("status", order?.orderStatus)
                i.putExtra("order", order)
                it.context.startActivity(i)
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
                Constants.ORDER_OUT_FOR_DELIVERY -> {
                    holder.txtOrderStatus.setBackgroundColor(context.resources.getColor(R.color.order_selected_text_color))
                    holder.txtOrderStatus.setTextColor(context.resources.getColor(R.color.white))
                    holder.txtOrderStatus.text = "Out for delivery"
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }
}