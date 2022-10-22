package com.surajmanshal.mannsignadmin.utils

import android.content.DialogInterface
import android.widget.EditText
import com.surajmanshal.mannsignadmin.URL
import com.surajmanshal.mannsignadmin.adapter.PricingAdapter
import com.surajmanshal.mannsignadmin.data.model.Area
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.ProductType
import com.surajmanshal.mannsignadmin.viewmodel.PricingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Functions {

    fun urlMaker(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/images/")
        return URL.IMAGE_PATH+ fileName
    }

}