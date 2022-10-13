package com.surajmanshal.mannsignadmin.data.model

import com.surajmanshal.response.SimpleResponse


data class LoginResponse(
    val simpleResponse: SimpleResponse,
    val user: User = User()
)
