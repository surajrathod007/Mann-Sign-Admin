package com.surajmanshal.mannsignadmin.data.model.ordering

data class TransactionItem(
    val transaction : Transaction,
    var visible : Boolean = false
)
