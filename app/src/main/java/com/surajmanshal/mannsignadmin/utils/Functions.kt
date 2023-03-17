package com.surajmanshal.mannsignadmin.utils

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.surajmanshal.mannsignadmin.URL
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Functions {

    fun urlMaker(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/images/")
        return URL.IMAGE_PATH+ fileName
    }
    fun urlMakerChat(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/chat/images/")
        return URL.CHAT_IMAGE_PATH+ fileName
    }
    fun setTypeNumber(editText: EditText){
        editText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    fun makeViewVisible(view : View){
        view.visibility = VISIBLE
    }
    fun makeViewGone(view : View){
        view.visibility = GONE
    }

    fun makeToast(context : Context, msg : String, long : Boolean=false){
        if(long){
            Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun timeStampToDate(timestamp : String): String {
        val date = Date(timestamp.toLong())
        val t = Timestamp(timestamp.toLong())
        val d = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), ZoneId.systemDefault())

        return d.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
    }

    fun Any.toResourceType() = when(this) {
        is Size -> ResourceType.Size
        is Material -> ResourceType.Material
        is Language -> ResourceType.Language
        else -> {ResourceType.Any}
    }

    fun enableOrDisableDialogPositiveButton(editTexts: List<EditText>, d: android.app.AlertDialog) {
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = editTexts.all { it.text.isNotEmpty() }
        /*et1.text.isNotEmpty()&&et2.text.isNotEmpty()&&et3.text.isNotEmpty()*/
    }
    fun enablePositiveBtnWhenValueChanged(initValue : String, editTexts: EditText, d: android.app.AlertDialog) {
        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = editTexts.text.toString() != initValue && editTexts.text.isNotEmpty()
    }

}