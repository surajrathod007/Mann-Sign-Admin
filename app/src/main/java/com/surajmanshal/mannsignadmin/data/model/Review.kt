package com.surajmanshal.mannsignadmin.data.model

import java.time.LocalDate

data class Review (
    val reviewId : Int?=null,
    val productId : Int,
    val rating : Int,
    val comment : String,
    val emailId : String,
    val reviewDate : String
)