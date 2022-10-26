package com.surajmanshal.mannsignadmin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductsBinding
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel

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

        // Views Setup
        with(binding){
            btnAddProduct.setOnClickListener {
                startActivity(Intent(this@ProductsActivity, ProductManagementActivity::class.java))
            }
        }
        vm.products.observe(this, Observer {
            setAdapterWithList(it)
        })
        vm.getPosters()
    }

    fun setAdapterWithList(list: List<Product>){
        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list)
    }

}