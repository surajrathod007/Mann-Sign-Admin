package com.surajmanshal.mannsignadmin.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import com.surajmanshal.mannsignadmin.databinding.ActivityMaterialManagementBinding
import com.surajmanshal.mannsignadmin.utils.*
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
    var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialManagementBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this)[ResourcesViewModel::class.java]
        setContentView(binding.root)

        with(binding) {

            btnSizeResources.setOnClickListener { sizeDialog.show(); hideButtons() }
            btnFontResource.setOnClickListener { fontDialog.show(); hideButtons() }
            btnMaterialResource.setOnClickListener { materialDialog.show(); hideButtons() }
            btnLanguageResource.setOnClickListener { languageDialog.show(); hideButtons() }

            sizeDialog.setOnClickListener { it.hide();showButtons() }
            fontDialog.setOnClickListener { it.hide();showButtons() }
            materialDialog.setOnClickListener { it.hide();showButtons() }
            languageDialog.setOnClickListener { it.hide();showButtons() }


            btnAddMaterial.setOnClickListener {
                if (etMaterialName.isFilled() && etMaterialPrice.isFilled()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.sendMaterial(
                            Material(
                                0,
                                etMaterialName.text.toString(),
                                etMaterialPrice.text.toString().toFloat()
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
        vm.serverResponse.observe(this) {
            if(it.success) clearFieldsAndHideDialogs()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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
                Constants.READ_EXTERNAL_STORAGE
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
        startActivityForResult(storageIntent, Constants.CHOOSE_FONT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CHOOSE_FONT) {
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
        hideDialogs()
        showButtons()
    }

    fun clearFields(){
        with(binding){
            etHeight.clear()
            etWidth.clear()
            etFileLink.clear()
            etLanguageName.clear()
            etMaterialName.clear()
            etMaterialPrice.clear()
        }
    }

    fun hideDialogs(){
        with(binding){
            fontDialog.hide()
            languageDialog.hide()
            materialDialog.hide()
            sizeDialog.hide()
        }
    }

}