package com.surajmanshal.mannsignadmin.data.model

import java.io.Serializable

data class Image(
    val id : Int,
    val url : String,
    val description : String? = null,
    val languageId : Int
) : Serializable
