package com.surajmanshal.mannsignadmin.ui

import android.content.Intent
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductsActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductsBinding
    val binding get() = _binding
    lateinit var vm : ProductsViewModel
    var columCount = 2
    val imagesFiles = mutableListOf<File>()

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
        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list,imagesFiles)
    }
    fun getImageFiles() {

        vm.products.value?.forEach { product ->

            product.images?.get(0)?.let {
                val response = repository.fetchImage(it.url)
                response.enqueue(object :
                    Callback<File> {
                    override fun onResponse(call: Call<File>, response: Response<File>) {
                        setAdapterWithList(vm.products.value!!)
                    }

                    override fun onFailure(call: Call<File>, t: Throwable) {
//                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
}