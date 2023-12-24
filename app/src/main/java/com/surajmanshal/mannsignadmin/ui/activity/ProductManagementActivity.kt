package com.surajmanshal.mannsignadmin.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.internal.LinkedTreeMap
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ProductDetailsImageAdapter
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
    private lateinit var _binding: ActivityProductManagementBinding
    private val binding get() = _binding
    private lateinit var vm: ProductManagementViewModel
    private lateinit var mProduct: Product
    private var imageUri: Uri? = null
    val mImages = mutableListOf<Image>()
    val pagerIndicator = PageIndicator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductManagementBinding.inflate(layoutInflater)
        intent.getSerializableExtra("product").let {
            mProduct = if (it == null) Product(0)
            else it as Product
        }
//        Toast.makeText(this, "$mProduct", Toast.LENGTH_SHORT).show()
        vm = ViewModelProvider(this).get(ProductManagementViewModel::class.java)

        var selectedCategory: Int? = 0
        CoroutineScope(Dispatchers.IO).launch {
            vm.setupViewModelDataMembers()
        }
        with(binding) {
            setContentView(root)

            rvProductImages.apply {
                layoutManager = LinearLayoutManager(
                    this@ProductManagementActivity,
                    RecyclerView.HORIZONTAL, false
                )
            }

            with(mProduct) {
                etProductCode.setText(productCode)
                etProductCode.setOnFocusChangeListener { view, isFocused ->
                    if (!isFocused) {
                        binding.tvPCodeExist.isVisible = vm.posters.value!!.find {
                            it.productCode == etProductCode.text.toString().trim()
                        } != null
                    }
                }
                posterDetails?.let {
                    etTitle.setText(it.title)
                    etShortDescription.setText(it.short_desc)
                    etLongDescription.setText(it.long_desc)
                }
            }

            btnAddProduct.apply {
                setOnClickListener {
                    mProduct.also { product ->
                        with(product) {
                            etProductCode.text.apply {
                                if (isNullOrBlank()) {
                                    Toast.makeText(
                                        this@ProductManagementActivity,
                                        "Product code is required",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    etProductCode.requestFocus()
                                    return@setOnClickListener
                                }
                                if (this.length > 15) {
                                    Toast.makeText(
                                        this@ProductManagementActivity,
                                        "Product code is $length long",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    etProductCode.requestFocus()
                                    return@setOnClickListener
                                }
                                productCode = this.toString()
                            }
                            if (etTitle.text.isNullOrEmpty()) {
                                Toast.makeText(
                                    this@ProductManagementActivity,
                                    "Title is required",
                                    Toast.LENGTH_SHORT
                                ).show()
                                etTitle.requestFocus()
                                return@setOnClickListener
                            }
                            if (etTitle.text.toString().length > 200) {
                                Toast.makeText(
                                    this@ProductManagementActivity,
                                    "Title is of ${etTitle.text.toString().length} chars",
                                    Toast.LENGTH_SHORT
                                ).show()
                                etTitle.requestFocus()
                                return@setOnClickListener
                            }
                            if (etLongDescription.text.toString().length < 64 && !etLongDescription.text.isNullOrEmpty()) {
                                Toast.makeText(
                                    this@ProductManagementActivity,
                                    "Write At least 64 characters",
                                    Toast.LENGTH_SHORT
                                ).show()
                                etLongDescription.requestFocus()
                                return@setOnClickListener
                            }
                            posterDetails = Poster(
                                title = textOf(etTitle),
                                short_desc = textOf(etShortDescription),
                                long_desc = textOf(etLongDescription)
                            )
                            sizes = getSelectedSizes(gvSizes)
                            if (sizes.isNullOrEmpty()) {
                                Toast.makeText(
                                    this@ProductManagementActivity,
                                    "Select At least one size",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                            materials = getSelectedMaterialsIds(gvMaterials)
                            if (materials.isNullOrEmpty()) {
                                Toast.makeText(
                                    this@ProductManagementActivity,
                                    "Select At least one Material",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            }
                            typeId = Constants.TYPE_POSTER
                            subCategory = selectedCategory
                            category =
                                vm.subCategories.value?.get(binding.categorySpinner.selectedItemPosition)?.mainCategoryId


                            if (mProduct.productId == 0) { // Insert
                                languages =
                                    getSelectedLanguagesIds(gvLanguages)   // May be reason of wrong language reference
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.productImages.value?.forEach {
                                        if (it.fileUri != null)
                                            setupImage(it) // Passively calls insert product after image insertion
                                    }
                                }
                            } else { // Update
                                CoroutineScope(Dispatchers.IO).launch {
                                    vm.updateProduct(mProduct)
                                }
                            }

                        }
                    }
                }
                text = if (mProduct.productId != 0) "Update Details" else "Add Product"
            }

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedCategory = vm.subCategories.value?.get(p2)!!.id
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        }

        // Observers
        vm.sizes.observe(this, Observer {
            setupSizesViews()
        })
        vm.materials.observe(this, Observer {
            setupMaterialViews()
        })
        vm.languages.observe(this, Observer { languages ->
            binding.rvProductImages.adapter = vm.productImages.value?.let {
                ProductImageAdapter(vm) {
                    chooseImage()
                }
            }
            if (!mProduct.isNewProduct()) {
                binding.rvProductImages.adapter = mProduct.images?.let { it1 ->
                    ProductDetailsImageAdapter(it1.map { image ->
                        Pair(
                            image.url,
                            languages?.find { it.id == image.languageId }?.name ?: Language(
                                0,
                                "Unavailable"
                            ).name
                        )
                    })
                }
            }
        })
        vm.subCategories.observe(this, Observer {
            setupSubcategoryViews()
        })
        vm.serverResponse.observe(this, Observer {
            if (it.success) {
                Toast.makeText(this@ProductManagementActivity, it.message, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.d("Product Management", it.message)
                Toast.makeText(this@ProductManagementActivity, "Retry", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        vm.productUploadResponse.observe(this, Observer {
            if (it.first.productId > 0) {
                Toast.makeText(this@ProductManagementActivity, "Product Added", Toast.LENGTH_SHORT)
                    .show()
                lifecycleScope.launch {
                    delay(1500)
                    onBackPressed()
                }
            } else {
                AlertDialog.Builder(this).setTitle("Failed to Upload")
                    .setMessage("Take a screen shot and share" + it.second)
                    .show()
            }
        })
        vm.imageUploadResponse.observe(this, Observer { response ->
            // todo : show some loading/progress ui
//            Toast.makeText(this@ProductManagementActivity, response.message, Toast.LENGTH_SHORT).show()
            val data = response.data as LinkedTreeMap<String, Any>
            mImages.add(
                Image(
                    id = data["id"].toString().toDouble().toInt(), url = data["url"].toString(),
                    description = data["description"].toString(),
                    data["languageId"].toString().toFloat().toInt()
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                mProduct.also {
                    it.images = mImages
                }
                if (vm.productImages.value!!.size - 1 == mImages.size)
                    vm.addProduct(mProduct)
            }
        })

        if (mProduct.isNewProduct()) {
            vm.productImages.observe(this) {
                binding.rvProductImages.apply {
                    adapter = ProductImageAdapter(vm) {
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

        val sp = listOf(
            "12 x 18",
            "18 x 24",
            "24 x 36",
            "36 x 48"
        )

        val sl = listOf(
            "18 x 12",
            "24 x 18",
            "36 x 24",
            "48 x 36"
        )
        binding.cbStandardPortrait.apply {
            setOnCheckedChangeListener { compoundButton, b ->
                setSelections(sp, binding.gvSizes, b)
            }
        }
        binding.cbStandardLandscape.apply {
            setOnCheckedChangeListener { compoundButton, b ->
                setSelections(sl, binding.gvSizes, b)
            }
        }
        val uvVinyl = listOf(
            "UV Print On Vinyl Sticker",
            "UV Vinyl Print On 3mm ACP Sheet",
            "UV Vinyl Print On 3mm Foam Sheet",
            "UV Vinyl Print On 5mm Foam Sheet",
            "Vinyl Laminated Sticker",
            "Vinyl Sticker On 3mm ACP Sheet",
            "Vinyl Sticker On 3mm Foam Sheet",
            "Vinyl Sticker On 5mm Foam Sheet",
        )
        binding.cbUVVinyl.apply {
            setOnCheckedChangeListener { compoundButton, b ->
                setSelections(uvVinyl, binding.gvMaterials, b)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProductsActivity::class.java))
        finish()
        super.onBackPressed()
    }

    // Utilities
    private suspend fun setupImage(imageLanguage: ImageLanguage) {

        val dir = applicationContext.filesDir
        val file = File(dir, "image${imageLanguage.languageId}.png")

        val outputStream = FileOutputStream(file)
        contentResolver.openInputStream(imageLanguage.fileUri!!)?.copyTo(outputStream)

        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val part = MultipartBody.Part.createFormData("product", file.name, requestBody)
        println("${part.body().contentType()}" + "}")

        vm.sendImage(part, imageLanguage.languageId!!)
    }

    private fun textOf(et: TextInputEditText): String {
        return et.text.toString()
    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val storageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // IT IS DEPRECATED FIND LATEST METHOD FOR IT
            startActivityForResult(storageIntent, Constants.CHOOSE_IMAGE_REQ_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_EXTERNAL_STORAGE_REQ_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CHOOSE_IMAGE_REQ_CODE) {
                if (data != null) {
                    try {
                        val selectedImageUri = data.data!!
                        // todo:  storeImgOnServer(selectedImageUri)
                        imageUri = selectedImageUri
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                            .apply {
                                setMargins(8, 0, 8, 0)
                            }

                        val dialog = android.app.AlertDialog.Builder(this)
                        dialog.setTitle("Select Poster language")
                        val dialogContentLayout = LinearLayout(this).apply {
                            orientation = LinearLayout.VERTICAL
                        }
                        dialogContentLayout.setPadding(32, 0, 32, 0)
                        var languageId = -1
                        val imageLanguageSelectionBinding =
                            ModernSpinnerLayoutBinding.inflate(layoutInflater).apply {
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
//                                Toast.makeText(this@ProductManagementActivity,"$languageId", Toast.LENGTH_SHORT).show()
                                vm.addImage(selectedImageUri, languageId)
                            }
                        })
                        dialog.show()
//                        Glide.with(this).load(selectedImageUri).into(binding.ivProduct)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Req canceled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun createCheckBox(text: String, checked: Boolean = false): CheckBox {
        val checkBox = CheckBox(this)
        checkBox.text = text
        checkBox.isChecked = checked
        return checkBox
    }

    fun setupSizesViews() {
        vm.sizes.value?.forEach {
            binding.gvSizes.addView(
                createCheckBox(
                    "${it.width} x ${it.height}",
                    mProduct.sizes?.contains(it) ?: false
                )
            )
        }
    }

    fun setupMaterialViews() {
        vm.materials.value?.forEach {
            binding.gvMaterials.addView(
                createCheckBox(
                    it.name,
                    mProduct.materials?.contains(it.id) ?: false
                )
            )
            Log.d("Material", it.name)
        }
    }

    fun setupLanguageViews() {
        binding.gvLanguages.removeAllViews()
        vm.productImages.value?.forEach { imgLang ->
            vm.languages.value?.find { it.id == imgLang.languageId }?.let {
                binding.gvLanguages.addView(createCheckBox(it.name).apply {
                    isChecked = true
                    isEnabled = false
                })
            }
        }
    }

    fun setupSubcategoryViews() {
        val list = mutableListOf<String>()
        vm.subCategories.value?.forEach {
            list.add(it.name)
        }

        binding.categorySpinner.apply {
            adapter = ArrayAdapter(
                this@ProductManagementActivity,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                list
            )
            setSelection(
                (adapter as ArrayAdapter<String>)
                    .getPosition(
                        vm.subCategories.value?.find { it.id == mProduct.subCategory }?.name
                    )
            )
        }
    }

    fun setSelections(items: List<String>, container: GridLayout, doSelect: Boolean) {
        container.forEach { view ->
            val option = view as CheckBox
            if (items.any { it == option.text.trim() }) {
                option.isChecked = doSelect
            }
        }
    }

    fun getSelectedSizes(container: GridLayout): List<Size> {
        val list = mutableListOf<Size>()
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                list.add(vm.sizes.value!!.get(container.indexOfChild(option)))
        }
        return list
    }

    fun getSelectedMaterialsIds(container: GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                list.add(vm.materials.value!!.get(container.indexOfChild(option)).id!!)
        }
        return list
    }

    fun getSelectedLanguagesIds(container: GridLayout): List<Int> {
        val list = mutableListOf<Int>()
        container.forEach {
            val option = it as CheckBox
            if (option.isChecked)
                vm.languages.value!!.find {
                    it.name == option.text.toString()
                }?.let { language ->
                    list.add(language.id)
                }
        }
        return list
    }

    fun createRadioButton(context: Context, text: String): RadioButton {
        return RadioButton(context).apply {
            setText(text)
        }
    }

    private fun Product.isNewProduct() = this.productId == 0

}