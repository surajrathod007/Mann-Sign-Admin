package com.surajmanshal.mannsignadmin.utils

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import com.surajmanshal.mannsignadmin.URL
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

}