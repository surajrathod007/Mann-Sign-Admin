package com.surajmanshal.mannsignadmin.data.model

data class Material(
    val id : Int? = null,
    var name : String = "",
    val price : Float,
    val productTypeId : Int
)
