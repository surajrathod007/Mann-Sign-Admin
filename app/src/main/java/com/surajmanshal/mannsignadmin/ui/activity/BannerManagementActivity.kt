package com.surajmanshal.mannsignadmin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.surajmanshal.mannsignadmin.adapter.recyclerView.AdBannerAdapter
import com.surajmanshal.mannsignadmin.data.model.AdBanner
import com.surajmanshal.mannsignadmin.databinding.ActivityBannerManagementBinding
import com.surajmanshal.mannsignadmin.utils.ImageUploading
import com.surajmanshal.mannsignadmin.utils.ResourceType
import com.surajmanshal.mannsignadmin.viewmodel.AdBannerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BannerManagementActivity : AppCompatActivity(), AdBannerAdapter.BannerClickListener {

    lateinit var binding: ActivityBannerManagementBinding
    lateinit var vm: AdBannerViewModel
    val imageUploading: ImageUploading by lazy {
        ImageUploading(this)
    }
    lateinit var bannerAdapter: AdBannerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[AdBannerViewModel::class.java].also {
            it.getBanners()
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.rvBanners.layoutManager = LinearLayoutManager(this)
        binding.rvBanners.adapter = AdBannerAdapter(this).also {
            bannerAdapter = it
        }

        binding.btnAddBanner.setOnClickListener {
            imageUploading.chooseImageFromGallary()
        }

        vm.adBanners.observe(this) {
            println(it.toString())
            bannerAdapter.setData(it.reversed())
        }

        imageUploading.imageUri.observe(this) {
            CoroutineScope(Dispatchers.IO).launch {
                imageUploading.sendImage(
                    ResourceType.AdBanner,
                    imageUploading.createImageMultipart()
                )
            }
        }

        imageUploading.imageUploadResponse.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            if (it.success)
                vm.getBanners()
        }

    }

    override fun onItemClick(banner: AdBanner) {
        startActivity(
            Intent(
                this,
                ImageViewingActivity::class.java
            ).apply {
                putExtra("imgUrl",banner.imgUrl)
            }
        )
    }

    override fun onDeleteClick(banner: AdBanner) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Alert !")
            .setMessage("Do you want to delete it ?")
            .setPositiveButton("Delete") { di, p1 -> vm.deleteBanner(banner) }
            .setNegativeButton("Cancel") { p0, p1 -> p0?.dismiss() }
            .setCancelable(true)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageUploading.CHOOSE_IMAGE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    imageUploading.imageUri.postValue(data.data)
                }
            } else {
                Toast.makeText(this, "Req Cancel", Toast.LENGTH_SHORT).show()
            }
        }
    }
}