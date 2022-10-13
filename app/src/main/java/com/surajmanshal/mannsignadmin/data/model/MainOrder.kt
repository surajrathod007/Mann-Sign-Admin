package com.surajmanshal.mannsignadmin.data.model



data class MainOrder(
    val mainOrderId : String,
    val orderItems : List<Order>
)
