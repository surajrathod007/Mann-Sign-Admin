package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewManager
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.adapter.CategoryAdapter
import com.surajmanshal.mannsignadmin.adapter.ProductsAdapter
import com.surajmanshal.mannsignadmin.data.model.Category
import com.surajmanshal.mannsignadmin.data.model.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.ui.fragments.AdapterActivity
import com.surajmanshal.mannsignadmin.viewmodel.CategoryViewModel

class CategoryManagementActivity : AdapterActivity() {
    private lateinit var _binding : ActivityCategoryManagementBinding
    val binding get() = _binding
    lateinit var vm : CategoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[CategoryViewModel::class.java]
        vm.getCategories()
        vm.categories.value?.let { setAdapterWithList(it,binding.rvCategories,CategoryAdapter(vm)) }

        with(binding){
            btnCancel.setOnClickListener {
                alertDialog.visibility = View.GONE
            }
            alertDialog.setOnClickListener {
                it.visibility = View.GONE
            }
            btnDelete.setOnClickListener {
                // send delete request
            }
        }
        vm.categories.observe(this, Observer {
            setAdapterWithList(it,binding.rvCategories,CategoryAdapter(vm))
        })
    }

}