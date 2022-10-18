package com.surajmanshal.mannsignadmin.data.model

import android.os.Parcelable
import java.io.Serializable

data class Order(
    val orderId : String,
    val emailId : String,
    val orderDate : String,
    val orderItems : List<OrderItem>? = null,
    val quantity : Int = 0,
    var trackingUrl : String?=null,
    var days : Int? = null,
    var orderStatus : Int = 0,
    var paymentStatus : Int = 0,
    val total : Float,
) : Serializable
