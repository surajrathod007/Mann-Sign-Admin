package com.surajmanshal.mannsignadmin.adapter.recyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.room.OrderCountDao
import com.surajmanshal.mannsignadmin.room.OrderCountEntity
import com.surajmanshal.mannsignadmin.ui.activity.OrderDetailsActivity
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.Functions.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersAdapter(val context: Context, val orders: List<Order>,val db : OrderCountDao? = null) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderTitle = itemView.findViewById<TextView>(R.id.txtOrderId)
        val txtOrderDate = itemView.findViewById<TextView>(R.id.txtOrderDate)
        val txtOrderEmailId = itemView.findViewById<TextView>(R.id.txtOrderEmailId)
        val txtOrderTotal = itemView.findViewById<TextView>(R.id.txtOrderTotal)
        val txtOrderStatus = itemView.findViewById<TextView>(R.id.txtOrderStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_layout,parent,false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.txtOrderTitle.text = order.orderId
        holder.txtOrderDate.text = order.orderDate.toString()
        holder.txtOrderEmailId.text = order.emailId
        holder.txtOrderTotal.text = "₹ ${order.total}"

        var isExists = true

        if(db!=null){
           // isExists = db.orderExists(order.orderId)
            try{
                orderExists(order.orderId){
                    isExists = it!=null
                    if(it!=null){
                        holder.txtOrderStatus.visibility = View.GONE
                    }else{
                        holder.txtOrderStatus.visibility = View.VISIBLE
                    }
                }
            }catch (e : Exception){
                makeToast(context,"${e.message}")
            }
        }else{
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
        holder.itemView.setOnClickListener {
            if(!isExists){
                CoroutineScope(Dispatchers.IO).launch {
                    db?.addOrder(OrderCountEntity(
                        order.orderId
                    ))
                }
            }
            //makeToast(context,"Count $isExists")
            val i = Intent(it.context, OrderDetailsActivity::class.java)
            i.putExtra("status",order.orderStatus)
            i.putExtra("order",order)
            it.context.startActivity(i)
        }




    }

    fun orderExists(oid : String,isExists : (OrderCountEntity?) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val d = db?.orderExists(oid)
            withContext(Dispatchers.Main){
                isExists(d)
            }
        }
    }
    override fun getItemCount(): Int {
        return orders.size
    }

}