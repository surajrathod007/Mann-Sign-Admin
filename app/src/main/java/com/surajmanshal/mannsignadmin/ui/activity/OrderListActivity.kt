package com.surajmanshal.mannsignadmin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.recyclerView.OrdersAdapter
import com.surajmanshal.mannsignadmin.data.model.ordering.Order
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderListBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.room.LocalDatabase
import com.surajmanshal.mannsignadmin.room.OrderCountDao
import com.surajmanshal.mannsignadmin.utils.Functions.makeToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderListActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderListBinding
    var orderStatus : Int? = null
    lateinit var db : OrderCountDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderListBinding.inflate(layoutInflater)
        db = LocalDatabase.getDatabase(this).orderCountDao()
        orderStatus = intent.getIntExtra("orderStatus",-1)
        if(orderStatus!=-1){
            loadOrders(orderStatus!!)
        }
        setContentView(binding.root)
    }

    private fun loadOrders(orderStatus: Int) {
        binding.progressLoadingOrders.visibility = View.VISIBLE
        val r = NetworkService.networkInstance.getOrdersByStatus(orderStatus.toString())
        r.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                if(response.body() != null){
                    val l = response.body()
                    if(!l.isNullOrEmpty())
                        binding.rvOrdersNew.adapter = OrdersAdapter(this@OrderListActivity,l!!,db)
                    else
                        binding.txtNoOrders.visibility = View.VISIBLE
                    //makeToast(this@OrderListActivity,l.toString())
                }
                binding.progressLoadingOrders.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
                binding.progressLoadingOrders.visibility = View.GONE
                makeToast(this@OrderListActivity,"${t.message}")
            }
        })
    }
}