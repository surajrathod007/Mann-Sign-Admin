package com.surajmanshal.mannsignadmin.data.model

data class Order(
    val orderId : Int,
    val emailId : String,
    val orderDate : String,
    val product: Product? = null,
    val variant : Variant? = null,
    val quantity : Int,
    val trackingUrl : String?=null,
    val days : Int? = null,
    val orderStatus : Int = 0,
    val paymentStatus : Int = 0,
    val total : Float,
    val mainOrderId : String
)
