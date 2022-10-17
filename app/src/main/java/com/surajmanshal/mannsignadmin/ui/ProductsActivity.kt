package com.surajmanshal.mannsignadmin.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsignadmin.adapter.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductsBinding
import com.surajmanshal.mannsignadmin.viewmodel.OrdersViewModel.Companion.repository
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductsActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductsBinding
    val binding get() = _binding
    lateinit var vm : ProductsViewModel
    var columCount = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]
        binding.rvProducts.layoutManager = GridLayoutManager(this,columCount)
        vm.getPosters()

        // Views Setup
        with(binding){
            btnAddProduct.setOnClickListener {
                startActivity(Intent(this@ProductsActivity, ProductManagementActivity::class.java))
            }
        }
        vm.products.observe(this, Observer {
            setAdapterWithList(it)
        })

    }

    fun setAdapterWithList(list: List<Product>){
        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list)
    }

}