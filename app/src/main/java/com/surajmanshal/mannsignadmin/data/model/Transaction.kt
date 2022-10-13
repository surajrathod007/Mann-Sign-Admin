package com.surajmanshal.mannsignadmin.data.model

data class Transaction(
    val billId : String,
    val transactionId : String,
    val emailId : String,
    val date : String,
    val amount : Float
)
