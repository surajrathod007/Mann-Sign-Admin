package com.surajmanshal.response



data class SimpleResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)
