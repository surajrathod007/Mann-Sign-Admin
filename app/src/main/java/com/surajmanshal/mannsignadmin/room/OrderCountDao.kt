package com.surajmanshal.mannsignadmin.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface OrderCountDao {
    @Insert(onConflict = REPLACE)
    fun addOrder(order: OrderCountEntity)

    @Query("SELECT * from order_count where orderId=:orderIds")
    fun orderExists(orderIds : String) : OrderCountEntity

}