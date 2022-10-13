package com.surajmanshal.mannsignadmin.data.model

data class ACPBoard(
    val text : String,
    val emboss : Float,
    val rgb : String, // CMYK could be generated at the time of storing it in table
    val font : Int
)
