package com.surajmanshal.mannsignadmin.data.model.ordering

import com.surajmanshal.mannsignadmin.data.model.Variant
import com.surajmanshal.mannsignadmin.data.model.product.Product

data class CartItem(
    val cartItemId : Int,
    val emailId : String,
    var product: Product? = null,
    var variant : Variant? = null,
    val quantity : Int,
    val totalPrice : Float
)
