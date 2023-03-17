package com.surajmanshal.mannsignadmin.data.model

import java.io.Serializable

data class Size(
    val sid: Int,
    var width: Int = 1,
    var height: Int = 1,
) : Serializable
