package com.surajmanshal.mannsignadmin.data.model

data class Variant(
    val variantId : Int? = null,
    val productId : Int,
    val sizeId : Int,
    val materialId : Int,
    val languageId : Int,
    val variantPrice : Float
)
