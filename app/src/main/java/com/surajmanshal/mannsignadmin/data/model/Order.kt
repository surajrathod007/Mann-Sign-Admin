package com.surajmanshal.mannsignadmin.data.model

import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDate

data class Order(
    val orderId : String,
    val emailId : String,
    val orderDate : LocalDate,
    val orderItems : List<OrderItem>? = null,
    val quantity : Int = 0,
    var trackingUrl : String?=null,
    var days : Int? = null,
    var orderStatus : Int = 0,
    var paymentStatus : Int = 0,
    var total : Float,
) : Serializable
