package com.surajmanshal.mannsignadmin.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.adapter.recyclerView.ResourceItemsAdapter
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.databinding.ActivityMaterialManagementBinding
import com.surajmanshal.mannsignadmin.databinding.DialogContainerBinding
import com.surajmanshal.mannsignadmin.utils.*
import com.surajmanshal.mannsignadmin.utils.Functions.enablePositiveBtnWhenValueChanged
import com.surajmanshal.mannsignadmin.utils.Functions.setTypeNumber
import com.surajmanshal.mannsignadmin.utils.Functions.toResourceType
import com.surajmanshal.mannsignadmin.viewmodel.ResourcesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream


class ResourcesManagementActivity : AppCompatActivity() {

    lateinit var binding: ActivityMaterialManagementBinding
    lateinit var vm: ResourcesViewModel
    private lateinit var inputFields : Array<EditText>
    private lateinit var dialogs : Array<View>
    private lateinit var viewResButtons : Array<TextView>
    var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this)[ResourcesViewModel::class.java]
        setContentView(binding.root)

        with(binding) {

            allResLayout.apply {
                root.layoutParams = FrameLayout.LayoutParams(0,0)
            }

            inputFields = arrayOf(etWidth,
                etHeight,
                etLanguageName,
                etMaterialName,
                etMaterialPrice,
                etFileLink
            )

            dialogs = arrayOf(
                fontDialog,
                sizeDialog,
                materialDialog,
                languageDialog
            )

            viewResButtons = arrayOf(
                btnViewSizes,
                btnViewMaterials,
                btnViewLanguages,
                btnViewFonts
            )

            btnSizeResources.setOnClickListener { sizeDialog.show(); hideButtons() }
            btnFontResource.setOnClickListener { fontDialog.show(); hideButtons() }
            btnMaterialResource.setOnClickListener { materialDialog.show(); hideButtons() }
            btnLanguageResource.setOnClickListener { languageDialog.show(); hideButtons() }

            dialogs.forEach { it ->
                it.apply {
                    setOnClickListener{
                        hideKeyboard();showButtons();hide()
                    }
                }
            }

            viewResButtons.forEach { textView->
                textView.setOnClickListener {
                    hideDialogs()
                    allResLayout.apply {
                        root.layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                        tvToolbar.text = when(viewResButtons.indexOf(textView)){
                            0 -> "Sizes"
                            1 -> "Materials"
                            2 -> "Languages"
                            3 -> "Fonts"
                            else -> "Resources"
                        }
                        when(viewResButtons.indexOf(textView)){
                            0 -> vm.getSizes()
                            1 -> vm.getMaterials(Constants.TYPE_ALL)
                            2 -> vm.getLanguages()
                        }
                        root.show()
                    }
                }
            }
            viewResButtons.last().setOnClickListener {
                Toast.makeText(
                    this@ResourcesManagementActivity,
                    "To be implemented",
                    Toast.LENGTH_SHORT
                ).show()
            }


            btnAddMaterial.setOnClickListener {
                if (etMaterialName.isFilled() && etMaterialPrice.isFilled()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.sendMaterial(
                            Material(
                                0,
                                etMaterialName.text.toString(),
                                etMaterialPrice.text.toString().toFloat(),
                                when(radioGroupProductType.checkedRadioButtonId){
                                    productTypePoster.id -> Constants.TYPE_POSTER
                                    productTypeBanner.id -> Constants.TYPE_BANNER
                                    else -> Constants.TYPE_POSTER
                                }
                            )
                        )
                    }
                } else emptyFieldsMsg()
            }

            btnAddLanguage.setOnClickListener {
                if (etLanguageName.isFilled()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.sendLanguage(
                            Language(
                                0,
                                etLanguageName.text.toString(),
                            )
                        )
                    }
                } else emptyFieldsMsg()
            }

            btnUploadSize.setOnClickListener {
                if (etWidth.isFilled() && etHeight.isFilled()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.sendSize(
                            Size(
                                0,
                                etWidth.text.toString().toInt(),
                                etHeight.text.toString().toInt()
                            )
                        )
                    }
                } else emptyFieldsMsg()
            }

            fileInputView.setEndIconOnClickListener { chooseFont() }
            btnUploadFont.setOnClickListener {
                if (fileUri != null) {
                    CoroutineScope(Dispatchers.IO).launch { setupFile() }
                } else if (etFileLink.text.toString() != "") {
                    // Todo : Download from given url and upload to server
                } else {
                    Toast.makeText(
                        this@ResourcesManagementActivity,
                        "Can't Upload",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        with(vm){
            serverResponse.observe(this@ResourcesManagementActivity) {
                if(it.success) clearFieldsAndHideDialogs()
                Toast.makeText(this@ResourcesManagementActivity, it.message, Toast.LENGTH_SHORT).show()
            }

            deletionResponse.observe(this@ResourcesManagementActivity) {
                if(it.success)
                    fetchResources(resourceMode.value)
                Toast.makeText(this@ResourcesManagementActivity, it.message, Toast.LENGTH_SHORT).show()
            }
            updateResponse.observe(this@ResourcesManagementActivity) {
                if(it.success)
                    fetchResources(resourceMode.value)
                Toast.makeText(this@ResourcesManagementActivity, it.message, Toast.LENGTH_SHORT).show()
            }

            sizes.observe(this@ResourcesManagementActivity){
                setAllResAdapter(it)
            }
            materials.observe(this@ResourcesManagementActivity){
                setAllResAdapter(it)
            }
            languages.observe(this@ResourcesManagementActivity){
                setAllResAdapter(it)
            }

        }

    }

    fun fetchResources(res : Any?){
        when(res){
            is Size -> vm.getSizes()
            is Material -> vm.getMaterials(Constants.TYPE_ALL)
            is Language -> vm.getLanguages()
        }
    }
    private suspend fun setupFile() {

        val dir = applicationContext.filesDir

        val file = File(dir, "${getFileName(fileUri!!)}")
        val outputStream = FileOutputStream(file)
        contentResolver.openInputStream(fileUri!!)?.copyTo(outputStream)

        val requestBody = RequestBody.create(MediaType.parse("font/ttf"), file)
        val part = MultipartBody.Part.createFormData("font", file.name, requestBody)
        println("${part.body().contentType()}" + "}")

        vm.sendFont(part)
    }

    private fun chooseFont() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openFileManager()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_EXTERNAL_STORAGE_REQ_CODE
            )
            openFileManager()
        }
    }

    private fun openFileManager() {
        val storageIntent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT;
            type = "font/*";
        }
        // IT IS DEPRECATED FIND LATEST METHOD FOR IT
        startActivityForResult(storageIntent, Constants.CHOOSE_FONT_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CHOOSE_FONT_REQ_CODE) {
                if (data != null) {
                    try {
                        fileUri = data.data!!
                        binding.etFileLink.apply {
                            setText("File Ready to Upload")
                            isEnabled = false
                        }

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

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun hideButtons() = binding.buttonsLayout.hide()

    fun showButtons() = binding.buttonsLayout.show()

    fun emptyFieldsMsg() = Toast.makeText(
        this@ResourcesManagementActivity,
        "Please fill the fields",
        Toast.LENGTH_SHORT
    ).show()

    fun clearFieldsAndHideDialogs(){
        clearFields()
        hideKeyboard()
        hideDialogs()
        showButtons()
    }

    fun clearFields(){
        inputFields.forEach { it.clear() }
    }

    fun hideDialogs(){
        with(binding){
            fontDialog.hide()
            languageDialog.hide()
            materialDialog.hide()
            sizeDialog.hide()
        }
    }

    fun hideKeyboard(){
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setAllResAdapter(list: List<Any>){
        binding.allResLayout.rvItems.adapter = ResourceItemsAdapter(list,
            { deletable ->
                CoroutineScope(Dispatchers.IO).launch{
                    vm.deleteResource(deletable)
                }
            }, { editable ->
                setUpResourceUpdateDialog(editable)
            }
        )
    }

    private fun setUpResourceUpdateDialog(resource: Any) {
        val dialog = android.app.AlertDialog.Builder(this@ResourcesManagementActivity)
        dialog.setTitle("Update ${resource.toResourceType().name}")

        val etField1 = EditText(this@ResourcesManagementActivity)
        val etField2 = EditText(this@ResourcesManagementActivity)
        var etField1Initial : String = ""
        var etField2Initial : String = ""
        val dialogContainerView = DialogContainerBinding.inflate(layoutInflater).also {
            it.dialogContainer.addView(etField1)
        }

        when(resource){
           is Size -> {
                etField1.apply {
                    hint = "Width"
                    setText(resource.width.toString())
                    etField1Initial = resource.width.toString()
                }
                etField2.apply{
                    hint = "Height"
                    setText(resource.height.toString())
                    etField2Initial = resource.height.toString()
                }
                setTypeNumber(etField1)
                setTypeNumber(etField2)
                dialogContainerView.dialogContainer.addView(etField2)
            }
           is Material -> {
                etField1.apply{
                    hint = "Material"
                    setText(resource.name)
                    etField1Initial = resource.name
                }
            }
           is Language -> {
                etField1.apply{
                    hint = "Language"
                    setText(resource.name)
                    etField1Initial = resource.name
                }
            }
        }

        dialog.setView(dialogContainerView.root)
        dialog.setPositiveButton("Update", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                CoroutineScope(Dispatchers.IO).launch{
                    resource.apply {
                        when(this){
                            is Size -> {
                                width = etField1.text.toString().toInt()
                                height = etField2.text.toString().toInt()
                            }
                            is Material -> {
                                name = etField1.text.toString()
                            }
                            is Language -> {
                                name = etField1.text.toString()
                            }
                        }
                    }
                    vm.updateResource(resource)
                }
            }
        })
        val d = dialog.create()

        etField1.doOnTextChanged { _, _, _, _ ->
            enablePositiveBtnWhenValueChanged(etField1Initial,etField1,d)
        }
        etField2.doOnTextChanged { _, _, _, _ ->
            enablePositiveBtnWhenValueChanged(etField2Initial,etField2,d)
        }

        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

    }

    override fun onBackPressed() {
        with(binding) {
            if (!binding.buttonsLayout.isVisible) {
                buttonsLayout.show()
                hideDialogs()
                allResLayout.apply {
                    root.layoutParams = FrameLayout.LayoutParams(0,0)
                }
            }else super.onBackPressed()
        }
    }
}