package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.OrderItemsAdapter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.OrderItem
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    lateinit var paymentStatusSpinner : Spinner
    lateinit var binding: ActivityOrderDetailsBinding
    var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        val i = intent
        status = i.getIntExtra("status",0)
        val order = i.getSerializableExtra("order") as Order
        setContentView(binding.root)
        spinner = findViewById(R.id.spOrderStatus)
        paymentStatusSpinner = findViewById(R.id.spOrderPaymentStatus)

        setupSpinner()
        setUpPaymentStatus()
        setupOrderDetails(order)
        setupOrderItems(order.orderItems)

    }

    private fun setupOrderItems(orderItems: List<OrderItem>?) {
        binding.rvOrderItems.adapter = OrderItemsAdapter(this@OrderDetailsActivity,orderItems!!)
    }

    private fun setupOrderDetails(order: Order) {
        with(binding){
            txtOrderIdDetails.text = order.orderId
            txtOrderDateDetails.text = order.orderDate
            txtOrderQuantityDetails.text = order.quantity.toString()
            txtOrderTotalDetails.text = "$"+order.total.toString()
            paymentStatusSpinner.setSelection(order.paymentStatus)
            spinner.setSelection(order.orderStatus)
        }
    }

    private fun setUpPaymentStatus() {
        val adp = ArrayAdapter(this@OrderDetailsActivity,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.order_payment_status))
        paymentStatusSpinner.adapter = adp
    }

    private fun setupSpinner() {
        val adp = ArrayAdapter(this@OrderDetailsActivity,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.order_status_array))
        spinner.adapter = adp

    }
}