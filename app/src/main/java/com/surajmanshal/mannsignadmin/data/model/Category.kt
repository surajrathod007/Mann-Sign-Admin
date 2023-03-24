package com.surajmanshal.mannsignadmin.data.model

data class Category(
    val id : Int? = null,
    var name : String="",
    val subCategories : List<SubCategory>? = null
)
