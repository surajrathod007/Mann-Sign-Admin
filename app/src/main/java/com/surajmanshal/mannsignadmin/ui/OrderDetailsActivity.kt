package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    var status = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent
        status = i.getIntExtra("status",0)
        setContentView(R.layout.activity_order_details)
        spinner = findViewById(R.id.spOrderStatus)
        setupSpinner()

    }

    private fun setupSpinner() {
        val adp = ArrayAdapter(this@OrderDetailsActivity,android.R.layout.simple_spinner_item,resources.getStringArray(R.array.order_status_array))
        spinner.adapter = adp
        spinner.setSelection(status)
    }
}