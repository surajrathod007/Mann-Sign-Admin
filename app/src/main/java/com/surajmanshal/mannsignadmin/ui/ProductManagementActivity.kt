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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm : ProductManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductManagementViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
        with(binding){
            setContentView(root)
            btnAddProduct.setOnClickListener {
                Toast.makeText(this@ProductManagementActivity, "added", Toast.LENGTH_SHORT).show()
            }
        }

        // Observers
        vm.materials.observe(this, Observer{
            setupMaterialViews()
        })
        vm.languages.observe(this, Observer{
            setupLanguageViews()
        })
        vm.subCategories.observe(this, Observer{
            setupSubcategoryViews()
        })

    }
    override fun onDestroy() {
        super.onDestroy()
    }

    // Utilities
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
    fun setupLanguageViews(){
        vm.languages.value?.forEach {
            binding.gvLanguages.addView(createCheckBox(it.name))
        }
    }
    fun setupSubcategoryViews(){
        // Todo : Spinner setup is required
    }


}