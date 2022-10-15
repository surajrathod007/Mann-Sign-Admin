package com.surajmanshal.mannsignadmin.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.GridView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.databinding.ActivityProductManagementBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm : ProductManagementViewModel
    private val mProduct = Product(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductManagementViewModel::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
        with(binding){
            setContentView(root)
            ivProduct.setOnClickListener {
                chooseImage()
            }
            btnAddProduct.setOnClickListener {
                mProduct.also{
                    with(it) {
                        images = listOf(Image(0, ""))
                        sizes = getSelectedSizes(gvSizes)
                        materials = getSelectedMaterialsIds(gvMaterials)
                        languages = getSelectedLanguagesIds(gvLanguages)
                        typeId = Constants.TYPE_POSTER
                        subCategory = getSubCategoryId()
                        category = vm.subCategories.value?.get(binding.categorySpinner.selectedItemPosition)?.mainCategoryId
                        posterDetails = Poster(
                            title = textOf(etTitle),
                            short_desc = textOf(etShortDescription),
                            long_desc = textOf(etLongDescription)
                        )
                    }
                }
                vm.addProduct(mProduct)
                Toast.makeText(this@ProductManagementActivity, "added", Toast.LENGTH_SHORT).show()
            }
        }

        // Observers
        vm.sizes.observe(this, Observer{
           setupSizesViews()
        })
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

    private fun textOf(et: TextInputEditText): String {
        return et.text.toString()
    }

    private fun getSubCategoryId(): Int? {
        return vm.subCategories.value?.get(binding.categorySpinner.selectedItemPosition)?.id
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // Utilities
    private fun chooseImage(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val storageIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // IT IS DEPRECATED FIND LATEST METHOD FOR IT
            startActivityForResult(storageIntent, Constants.CHOOSE_IMAGE)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_EXTERNAL_STORAGE)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK){
            if(requestCode == Constants.CHOOSE_IMAGE){
                if(data!=null){
                    try{
                        val selectedImageUri = data.data!!
                        // todo:  storeImgOnServer(selectedImageUri)

                        Glide.with(this).load(selectedImageUri).into(binding.ivProduct)
                    }catch(e : Exception){
                        e.printStackTrace()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Req canceled", Toast.LENGTH_SHORT).show()
        }
       super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createCheckBox(text : String): CheckBox {
        val checkBox = CheckBox(this)
        checkBox.text = text
        return checkBox
    }

    fun setupSizesViews(){
        vm.sizes.value?.forEach {
            binding.gvSizes.addView(createCheckBox("${it.width} x ${it.height}"))
        }
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
        val list = mutableListOf<String>()
        vm.subCategories.value?.forEach {
            list.add(it.name)
        }
        binding.categorySpinner.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list)
    }

    fun getSelectedSizes(container : GridLayout): List<Size> {
        val list = mutableListOf<Size>()
        for (i in 0..container.childCount){
            val option = container.getChildAt(i) as CheckBox
            if (option.isChecked)
                list.add(vm.sizes.value!!.get(i))
        }
        return list
    }
    fun getSelectedMaterialsIds(container : GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        for (i in 0..container.childCount){
            val option = container.getChildAt(i) as CheckBox
            if (option.isChecked)
                list.add(vm.materials.value!!.get(i).id)
        }
        return list
    }
    fun getSelectedLanguagesIds(container : GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        for (i in 0..container.childCount){
            val option = container.getChildAt(i) as CheckBox
            if (option.isChecked)
                list.add(vm.languages.value!!.get(i).id)
        }
        return list
    }
}