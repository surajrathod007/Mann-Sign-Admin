package com.surajmanshal.mannsignadmin.data.model

data class CartItem(
    val cartItemId : Int,
    val emailId : String,
    var product: Product? = null,
    var variant : Variant? = null,
    val quantity : Int,
    val totalPrice : Float
)
