package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.databinding.ActivityProductManagementBinding

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        with(binding){
            setContentView(root)



            btnAddProduct.setOnClickListener {
                Toast.makeText(this@ProductManagementActivity, "added", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}