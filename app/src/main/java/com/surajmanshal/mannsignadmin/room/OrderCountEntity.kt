package com.surajmanshal.mannsignadmin.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_count")
data class OrderCountEntity(
    @PrimaryKey
    val orderId : String
)
