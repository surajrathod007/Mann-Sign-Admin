package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductsBinding
import com.surajmanshal.mannsignadmin.ui.fragments.ProductDetailsFragment
import com.surajmanshal.mannsignadmin.ui.fragments.ProductsListFragment
import com.surajmanshal.mannsignadmin.viewmodel.ProductsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityProductsBinding
    val binding get() = _binding
    lateinit var vm: ProductsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]

//        vm.getPosters() // removed for paging
//        vm.getPosters(1) // removed for paging
        getMorePosters()

        val quoteReq = intent?.data?.pathSegments?.let {
            val params: List<String> = intent?.data?.pathSegments ?: listOf()
            if (params.size == 4) {
                params
            } else {
                null
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            while (vm.products.value == null){
                    delay(100)
            }
            withContext(Dispatchers.Main){
                if(quoteReq==null) replaceFragment(0, null)
                else replaceFragment(1, vm.products.value!!.find { it.productId == quoteReq.first().toInt() })
                vm.quoteReq = quoteReq
            }
        }
    }

    fun replaceFragment(code: Int, product: Product?) {
        product?.let { vm._currentProduct.value = product }
        val fragment = if (code == 0) ProductsListFragment.newInstance(vm, this)
        else ProductDetailsFragment.newInstance(vm)
        supportFragmentManager.beginTransaction().setTransition(TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(
                binding.fragmentContainerView.id,
                fragment
            ).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) finish()
        vm._currentProductLanguage.value = null
        vm._currentProductMaterial.value = null
    }

    fun getMorePosters() {
        vm.isLoading = true
        vm.getMorePosters()
    }

}