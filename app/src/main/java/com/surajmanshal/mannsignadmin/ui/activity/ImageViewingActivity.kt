package com.surajmanshal.mannsignadmin.ui.activity

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.surajmanshal.mannsignadmin.R
import com.surajmanshal.mannsignadmin.SecuredScreenActivity
import com.surajmanshal.mannsignadmin.databinding.FragmentViewProfilePicBinding
import com.surajmanshal.mannsignadmin.utils.Functions

class ImageViewingActivity : SecuredScreenActivity() {

    lateinit var binding: FragmentViewProfilePicBinding
    var imgUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentViewProfilePicBinding.inflate(layoutInflater)
        imgUrl = intent.getStringExtra("imgUrl")
        Glide.with(this@ImageViewingActivity).load(
            imgUrl?.let { Functions.urlMaker(it) }
        ).into(binding.imgProfilePicFrag)
        setContentView(binding.root)
        binding.toolbar.ivBackButton.apply {
            imageTintList = AppCompatResources
                .getColorStateList(this@ImageViewingActivity, R.color.white)
            setOnClickListener {
                onBackPressed()
            }
        }
    }

}