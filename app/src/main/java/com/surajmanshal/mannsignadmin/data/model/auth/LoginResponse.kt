package com.surajmanshal.mannsignadmin.data.model.auth

import com.surajmanshal.response.SimpleResponse


data class LoginResponse(
    val simpleResponse: SimpleResponse,
    val user: User = User()
)
