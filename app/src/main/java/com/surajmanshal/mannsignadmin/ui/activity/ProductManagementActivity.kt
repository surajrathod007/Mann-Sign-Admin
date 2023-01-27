package com.surajmanshal.mannsignadmin.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.internal.LinkedTreeMap
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductImageAdapter
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.data.model.product.Poster
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.databinding.ActivityProductManagementBinding
import com.surajmanshal.mannsignadmin.databinding.ModernSpinnerLayoutBinding
import com.surajmanshal.mannsignadmin.utils.Constants
import com.surajmanshal.mannsignadmin.utils.PageIndicator
import com.surajmanshal.mannsignadmin.viewmodel.ProductManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream


class ProductManagementActivity : AppCompatActivity() {
    private lateinit var _binding : ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm : ProductManagementViewModel
    private val mProduct = Product(0)
    private var imageUri : Uri? =null
    val mImages = mutableListOf<Image>()
    val pagerIndicator = PageIndicator()

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
           /* ivProduct.setOnClickListener {
                chooseImage()
            }*/
            btnAddProduct.setOnClickListener {
                mProduct.also{ product ->
                    with(product) {
//                        val uploadedImages = setupImage()
                        CoroutineScope(Dispatchers.IO).launch {
                            vm.productImages.value?.forEach {
                                if(it.fileUri!=null)
                                    setupImage(it)
                            }
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
//            val snapHelper = PagerSnapHelper()
            rvProductImages.apply {
                layoutManager = LinearLayoutManager(this@ProductManagementActivity,
                    RecyclerView.HORIZONTAL,false)
//                snapHelper.attachToRecyclerView(this)
            }

        }

        // Observers
        vm.sizes.observe(this, Observer{
            setupSizesViews()
        })
        vm.materials.observe(this, Observer{
            setupMaterialViews()
        })
        vm.languages.observe(this, Observer{ languages ->
            binding.rvProductImages.adapter = vm.productImages.value?.let { ProductImageAdapter(vm){
                chooseImage()
            }}
        })
        vm.subCategories.observe(this, Observer{
            setupSubcategoryViews()
        })
        vm.serverResponse.observe(this, Observer {

        })
        vm.productUploadResponse.observe(this, Observer {
            if(it.success) {
                Toast.makeText(this@ProductManagementActivity, "Product Added", Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    delay(1500)
                    onBackPressed()
                }
            }
        })
        vm.imageUploadResponse.observe(this, Observer { response ->
            // todo : show some loading/progress ui
            Toast.makeText(this@ProductManagementActivity, response.message, Toast.LENGTH_SHORT).show()
            val data = response.data as LinkedTreeMap<String,Any>
            mImages.add(Image(id = data["id"].toString().toDouble().toInt(), url = data["url"].toString(),
                description = data["description"].toString(),
                data["languageId"].toString().toFloat().toInt()
            ))
            CoroutineScope(Dispatchers.IO).launch {
                mProduct.also {
                    it.images = mImages
                }
                if(vm.productImages.value!!.size-1==mImages.size)
                    vm.addProduct(mProduct)
            }
        })

        vm.productImages.observe(this){
            binding.rvProductImages.apply {
                adapter = ProductImageAdapter(vm){
                    chooseImage()
                }
//                pagerIndicator.setItemsCount(it.size)
////                removeItemDecoration(pagerIndicator)
//                invalidateItemDecorations()
//                addItemDecoration(pagerIndicator)
//                onResume()
            }
            setupLanguageViews()
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this,ProductsActivity::class.java))
        finish()
        super.onBackPressed()
    }

    // Utilities
    private suspend fun setupImage(imageLanguage: ImageLanguage) {

        val dir = applicationContext.filesDir
        val file = File(dir, "image${imageLanguage.languageId}.png")

        val outputStream = FileOutputStream(file)
        contentResolver.openInputStream(imageLanguage.fileUri!!)?.copyTo(outputStream)

        val requestBody = RequestBody.create(MediaType.parse("image/jpg"),file)
        val part = MultipartBody.Part.createFormData("product",file.name,requestBody)
        println("${part.body().contentType()}" +"}")

        vm.sendImage(part,imageLanguage.languageId!!)
    }

    private fun textOf(et: TextInputEditText): String {
        return et.text.toString()
    }
    private fun chooseImage(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val storageIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // IT IS DEPRECATED FIND LATEST METHOD FOR IT
            startActivityForResult(storageIntent, Constants.CHOOSE_IMAGE_REQ_CODE)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_EXTERNAL_STORAGE_REQ_CODE)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK){
            if(requestCode == Constants.CHOOSE_IMAGE_REQ_CODE){
                if(data!=null){
                    try{
                        val selectedImageUri = data.data!!
                        // todo:  storeImgOnServer(selectedImageUri)
                        imageUri = selectedImageUri
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                            .apply {
                                setMargins(8,0,8,0)
                            }

                        val dialog = android.app.AlertDialog.Builder(this)
                        dialog.setTitle("Select Poster language")
                        val dialogContentLayout = LinearLayout(this).apply {
                            orientation = LinearLayout.VERTICAL
                        }
                        dialogContentLayout.setPadding(32,0,32,0)
                        var languageId = -1
                        val imageLanguageSelectionBinding = ModernSpinnerLayoutBinding.inflate(layoutInflater).apply {
                            vm.languages.value?.let { languageList ->
                                resSpinner.apply {
                                    setText(languageList[0].name)
                                    languageId = languageList[0].id
                                    setAdapter(
                                        ArrayAdapter(this@ProductManagementActivity,
                                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                            languageList.map { it.name })
                                    )
                                    setOnItemClickListener { adapterView, view, i, l ->
                                        languageId = languageList[i].id
                                    }
                                }
                            }
                        }

                        dialogContentLayout.addView(imageLanguageSelectionBinding.root)

                        dialog.setView(dialogContentLayout)
                        dialog.setCancelable(false)
                        dialog.setPositiveButton("Set", object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                Toast.makeText(this@ProductManagementActivity,"$languageId", Toast.LENGTH_SHORT).show()
                                vm.addImage(selectedImageUri,languageId)
                            }
                        })
                        dialog.show()
//                        Glide.with(this).load(selectedImageUri).into(binding.ivProduct)
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
        binding.gvLanguages.removeAllViews()
        vm.productImages.value?.forEach { imgLang->
            vm.languages.value?.find { it.id == imgLang.languageId }?.let {
                binding.gvLanguages.addView(createCheckBox(it.name).apply {
                    isChecked = true
                    isEnabled = false
                })
            }
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
                list.add(vm.materials.value!!.get(container.indexOfChild(option)).id!!)
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

    fun createRadioButton(context: Context, text : String): RadioButton {
        return RadioButton(context).apply {
            setText(text)
        }
    }

}