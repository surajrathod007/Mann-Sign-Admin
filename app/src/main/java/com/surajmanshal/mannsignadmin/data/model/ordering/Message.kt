package com.surajmanshal.mannsignadmin.data.model.ordering


import com.surajmanshal.mannsignadmin.data.model.auth.User

data class Message(
    val message : String,
    val sender : User,
    val createdAt : Long
)
