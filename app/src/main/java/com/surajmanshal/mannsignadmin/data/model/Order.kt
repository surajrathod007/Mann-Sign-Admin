package com.surajmanshal.mannsignadmin.data.model

import android.os.Parcelable
import java.io.Serializable

data class Order(
    val orderId : String,
    val emailId : String,
    val orderDate : String,
    val orderItems : List<OrderItem>? = null,
    val quantity : Int = 0,
    val trackingUrl : String?=null,
    val days : Int? = null,
    val orderStatus : Int = 0,
    val paymentStatus : Int = 0,
    val total : Float,
) : Serializable
