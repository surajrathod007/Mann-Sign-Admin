package com.surajmanshal.mannsignadmin.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajmanshal.mannsignadmin.adapter.CategoryAdapter
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
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
//        vm.categories.value?.let { setAdapterWithList(it,binding.rvCategories,CategoryAdapter(vm)) }

        with(binding){
            btnCancel.setOnClickListener {
                vm.deletionCancelOrDone()
            }
            alertDialog.setOnClickListener {
                vm.deletionCancelOrDone()
            }
            btnDelete.setOnClickListener {
                // send delete request
                Toast.makeText(this@CategoryManagementActivity, "Deleted", Toast.LENGTH_SHORT).show()
                vm.deletionCancelOrDone()
            }
        }
        vm.categories.observe(this, Observer {
            setAdapterWithList(it,binding.rvCategories,CategoryAdapter(vm))
        })
        vm.isDeleting.observe(this, Observer {
             binding.alertDialog.visibility = if (it) View.VISIBLE else View.GONE
        })
    }
}