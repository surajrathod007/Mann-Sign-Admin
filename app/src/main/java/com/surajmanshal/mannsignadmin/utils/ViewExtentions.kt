package com.surajmanshal.mannsignadmin.utils

import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible

fun View.show(){
    this.isVisible = true
}

fun View.hide(){
    this.isVisible = false
}

fun EditText.isFilled() = this.text.isNotEmpty()

fun EditText.clear() = this.text.clear()

fun EditText.setInputTypeDecimalNumbers(){
    this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
}
