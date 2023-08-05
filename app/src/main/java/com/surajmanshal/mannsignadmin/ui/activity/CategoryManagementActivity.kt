package com.surajmanshal.mannsignadmin.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.surajmanshal.mannsignadmin.databinding.ActivityCategoryManagementBinding
import com.surajmanshal.mannsignadmin.utils.ImageUploading

class CategoryManagementActivity : AdapterActivity() {
    private lateinit var _binding : ActivityCategoryManagementBinding
    val binding get() = _binding
    /*lateinit var vm : CategoryViewModel*/
    lateinit var imageUploading : ImageUploading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCategoryManagementBinding.inflate(layoutInflater)
        initImageUploader()
        setContentView(binding.root)
    }

    fun initImageUploader(){
        imageUploading = ImageUploading(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            println("$requestCode$resultCode ${data.data}")
        }else{
            Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show()
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageUploading.CHOOSE_IMAGE_REQ_CODE) {
                if (data != null) {
                    try {
                        imageUploading.imageUri.postValue((data.data!!))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Req canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}