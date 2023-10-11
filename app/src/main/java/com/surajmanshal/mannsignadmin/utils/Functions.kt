package com.surajmanshal.mannsignadmin.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.surajmanshal.mannsignadmin.URL
import com.surajmanshal.mannsignadmin.data.model.Language
import com.surajmanshal.mannsignadmin.data.model.Material
import com.surajmanshal.mannsignadmin.data.model.Size
import java.io.File
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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

    fun getFinancialYearString(date : LocalDate): String {
        return "${date.year}-${date.year + 1}"
    }

    fun getFormatedTimestamp(millis: Long, format: String): String? {
        val instant = Instant.ofEpochMilli(millis)
        val formatter = DateTimeFormatter.ofPattern(format)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

}

fun Float.getTwoDecimalValue(): String {
    /*
    val decimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
    decimalFormat.roundingMode = RoundingMode.DOWN
    return decimalFormat.format(this)

     */
    return "Rs "+String.format("%.2f", this)
}

fun Context.openFile(file: File, path: String) {
    val intent = Intent(Intent.ACTION_VIEW)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)
        intent.setData(uri)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    } else {
        intent.setDataAndType(Uri.parse(path), "application/pdf")
        val i = Intent.createChooser(intent, "Open File With")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }
}
