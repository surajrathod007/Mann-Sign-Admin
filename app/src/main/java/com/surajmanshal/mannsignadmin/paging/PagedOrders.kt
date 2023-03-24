package com.surajmanshal.mannsignadmin.paging

import com.surajmanshal.mannsignadmin.data.model.ordering.Order

data class PagedOrders(
    val count : Int,
    val totalCount : Int,
    val page : Int,
    val totalPages : Int,
    val results : List<Order>
)
