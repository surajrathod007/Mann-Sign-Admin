package com.surajmanshal.mannsignadmin.data.model

import java.io.Serializable

data class OrderItem(
    var product: Product? = null,
    var variant : Variant? = null,
    var quantity : Int,
    var totalPrice : Float
) : Serializable
