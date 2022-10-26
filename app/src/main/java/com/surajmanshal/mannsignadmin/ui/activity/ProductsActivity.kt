package com.surajmanshal.mannsignadmin.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductsBinding
import com.surajmanshal.mannsignadmin.ui.fragments.ProductDetailsFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ProductsListFragment
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel

class ProductsActivity : AppCompatActivity() {


    private lateinit var _binding : ActivityProductsBinding
    val binding get() = _binding
    lateinit var vm : ProductsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]

        replaceFragment(0,null)
//        binding.rvProducts.layoutManager = GridLayoutManager(this,columCount)
//
//        // Views Setup
//        with(binding){
//            btnAddProduct.setOnClickListener {
//                startActivity(Intent(this@ProductsActivity, ProductManagementActivity::class.java))
//            }
//        }
//        vm.products.observe(this, Observer {
//            setAdapterWithList(it)
//        })
//        vm.getPosters()
    }
//
//    fun setAdapterWithList(list: List<Product>){
//        if (list.isNotEmpty()) binding.rvProducts.adapter = ProductsAdapter(list)
//    }
    fun replaceFragment(code : Int,product: Product?){
        val fragment = if(code==0)ProductsListFragment.newInstance(vm,this)
        else ProductDetailsFragment.newInstance(vm,product!!,this)
        supportFragmentManager.beginTransaction().addToBackStack(null)
            .replace(binding.fragmentContainerView.id,
            fragment).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount==0) finish()
    }

}