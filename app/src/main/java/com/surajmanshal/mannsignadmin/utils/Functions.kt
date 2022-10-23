package com.surajmanshal.mannsignadmin.utils

import android.text.InputType
import android.widget.EditText
import com.surajmanshal.mannsignadmin.URL

object Functions {

    fun urlMaker(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/images/")
        return URL.IMAGE_PATH+ fileName
    }
    fun setTypeNumber(editText: EditText){
        editText.inputType = InputType.TYPE_CLASS_NUMBER
    }
}