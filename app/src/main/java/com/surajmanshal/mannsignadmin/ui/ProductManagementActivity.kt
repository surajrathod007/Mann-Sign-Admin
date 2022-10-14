package com.surajmanshal.mannsignadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.databinding.ActivityProductManagementBinding
import com.surajmanshal.mannsignadmin.network.NetworkService
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm : ProductManagementViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductManagementViewModel::class.java)
        vm.setupViewModelDataMembers()

        with(binding){
            setContentView(root)
            btnAddProduct.setOnClickListener {
                Toast.makeText(this@ProductManagementActivity, "added", Toast.LENGTH_SHORT).show()
            }
        }
        vm.materials.observe(this, Observer{
            setupMaterialViews()
        })

    }

    private fun createCheckBox(text : String): CheckBox {
        val checkBox = CheckBox(this)
        checkBox.text = text
        return checkBox
    }

    fun setupMaterialViews(){
        vm.materials.value?.forEach {
            binding.gvMaterials.addView(createCheckBox(it.name))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}