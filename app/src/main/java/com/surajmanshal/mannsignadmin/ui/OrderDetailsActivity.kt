package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.OrderItemsAdapter
import com.surajmanshal.mannsignadmin.data.model.Order
import com.surajmanshal.mannsignadmin.data.model.OrderItem
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var spinner: Spinner
    lateinit var paymentStatusSpinner: Spinner
    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var vm: OrdersViewModel
    var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val i = intent
        status = i.getIntExtra("status", 0)
        val order = i.getSerializableExtra("order") as Order
        setContentView(binding.root)
        spinner = findViewById(R.id.spOrderStatus)
        paymentStatusSpinner = findViewById(R.id.spOrderPaymentStatus)

        CoroutineScope(Dispatchers.IO).launch {
            vm.fetchUserByEmail(order.emailId)
        }
        setupSpinner()
        setUpPaymentStatus()
        setupOrderDetails(order)
        setupOrderItems(order.orderItems)

        vm.serverResponse.observe(this) {
            Toast.makeText(this@OrderDetailsActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

        binding.btnUpdateOrder.setOnClickListener {
            order.also {
                it.orderStatus = spinner.selectedItemPosition
                it.paymentStatus = paymentStatusSpinner.selectedItemPosition
                if (binding.edEstimatedDays.text.isNullOrEmpty()) {
                    it.days = 0
                } else {
                    it.days = Integer.parseInt(binding.edEstimatedDays.text.toString())
                }
                it.trackingUrl = binding.edTrackingUrl.text.toString()
            }
            CoroutineScope(Dispatchers.IO).launch {
                vm.updateOrder(order)
            }
        }

        vm.user.observe(this) {
            with(binding) {
                txtCustomerEmail.text = it.emailId
                txtCustomerFirstName.text = it.firstName
                txtCustomerLastName.text = it.lastName
                txtCustomerPhoneNumber.text = it.phoneNumber
                txtCustomerAddress.text = it.address
                txtCustomerPinCode.text = it.pinCode.toString()
            }
        }
    }


    private fun setupOrderItems(orderItems: List<OrderItem>?) {
        binding.rvOrderItems.adapter = OrderItemsAdapter(this@OrderDetailsActivity, orderItems!!)
    }

    private fun setupOrderDetails(order: Order) {

        with(binding) {
            txtOrderIdDetails.text = order.orderId
            txtOrderDateDetails.text = order.orderDate.toString()
            txtOrderQuantityDetails.text = order.quantity.toString()
            txtOrderTotalDetails.text = "₹" + order.total.toString()
            if (order.days != null)
                edEstimatedDays.setText(order.days.toString())
            if (!order.trackingUrl.isNullOrEmpty()) {
                edTrackingUrl.setText(order.trackingUrl)
            }
            paymentStatusSpinner.setSelection(order.paymentStatus)
            spinner.setSelection(order.orderStatus)
        }
    }

    private fun setUpPaymentStatus() {
        val adp = ArrayAdapter(
            this@OrderDetailsActivity,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.order_payment_status)
        )
        paymentStatusSpinner.adapter = adp
    }

    private fun setupSpinner() {
        val adp = ArrayAdapter(
            this@OrderDetailsActivity,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.order_status_array)
        )
        spinner.adapter = adp

    }
}