package com.surajmanshal.mannsignadmin.data.model

data class Order(
    val orderId : String,
    val emailId : String,
    val orderDate : String,
    val product: List<Product?>? = null,
    val variant : List<Variant?>? = null,
    val quantity : Int = 0,
    val trackingUrl : String?=null,
    val days : Int? = null,
    val orderStatus : Int = 0,
    val paymentStatus : Int = 0,
    val total : Float,
)
