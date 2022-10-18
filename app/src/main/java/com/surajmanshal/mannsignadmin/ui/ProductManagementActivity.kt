package com.surajmanshal.mannsignadmin.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
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
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm : ProductManagementViewModel
    private val mProduct = Product(0)
    private lateinit var imageUri : Uri
    val mImages = mutableListOf<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductManagementViewModel::class.java)
        var selectedCategory : Int? = 0
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
        with(binding){
            setContentView(root)
            Glide.with(this@ProductManagementActivity).load("https://ik.imagekit.io/eobmcqpoq/GREEN_NITA_ysnIaZNQz.png?ik-sdk-version=javascript-1.4.3&updatedAt=1665844404231").into(ivProduct)
            ivProduct.setOnClickListener {
                chooseImage()
            }
            btnAddProduct.setOnClickListener {
                mProduct.also{
                    with(it) {
//                        val uploadedImages = setupImage()
                        CoroutineScope(Dispatchers.IO).launch {
                            setupImage()
                        }
                        sizes = getSelectedSizes(gvSizes)
                        materials = getSelectedMaterialsIds(gvMaterials)
                        languages = getSelectedLanguagesIds(gvLanguages)
                        typeId = Constants.TYPE_POSTER
                        subCategory = selectedCategory
                        category = vm.subCategories.value?.get(binding.categorySpinner.selectedItemPosition)?.mainCategoryId
                        posterDetails = Poster(
                            title = textOf(etTitle),
                            short_desc = textOf(etShortDescription),
                            long_desc = textOf(etLongDescription)
                        )
//                        if(uploadedImages.isNotEmpty()) images = uploadedImages else return@setOnClickListener
                    }
                }
            }
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCategory = vm.subCategories.value?.get(p2)!!.id
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
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
        vm.serverResponse.observe(this, Observer {
            Toast.makeText(this@ProductManagementActivity, it.message, Toast.LENGTH_SHORT).show()
        })
        vm.productUploadResponse.observe(this, Observer {
            // todo : show some loading/progress ui
        })
        vm.imageUploadResponse.observe(this, Observer { response ->
            // todo : show some loading/progress ui
            val data = response.data as LinkedTreeMap<String,Any>
            mImages.add(Image(id = data["id"].toString().toDouble().toInt(), url = data["url"].toString(), description = data["description"].toString()))
            CoroutineScope(Dispatchers.IO).launch {
                mProduct.also {
                    it.images = mImages
                }
                vm.addProduct(mProduct)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }


    // Utilities
    private suspend fun setupImage() {

        val dir = applicationContext.filesDir
        val file = File(dir, "image.png")

        val outputStream = FileOutputStream(file)
        contentResolver.openInputStream(imageUri)?.copyTo(outputStream)

        val requestBody = RequestBody.create(MediaType.parse("image/jpg"),file)
        val part = MultipartBody.Part.createFormData("product",file.name,requestBody)
        println("${part.body().contentType()}" +"}")

        vm.sendImage(part)
    }

    private fun textOf(et: TextInputEditText): String {
        return et.text.toString()
    }
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
                        imageUri = selectedImageUri

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
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                list.add(vm.sizes.value!!.get(container.indexOfChild(option)))
        }
        return list
    }
    fun getSelectedMaterialsIds(container : GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                list.add(vm.materials.value!!.get(container.indexOfChild(option)).id)
        }
        return list
    }
    fun getSelectedLanguagesIds(container : GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                list.add(vm.languages.value!!.get(container.indexOfChild(option)).id)
        }
        return list
    }
}