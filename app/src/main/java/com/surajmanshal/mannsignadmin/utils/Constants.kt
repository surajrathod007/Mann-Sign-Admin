package com.surajmanshal.mannsignadmin.utils

object Constants {

    const val TYPE_POSTER = 1
    const val TYPE_BANNER = 2
    const val TYPE_ACP_BOARD = 3

    const val ORDER_PENDING = 0
    const val ORDER_CONFIRMED = 1
    const val ORDER_PROCCESSING = 2
    const val ORDER_READY = 3
    const val ORDER_DELIVERED = 4
    const val ORDER_CANCELED = 5

    const val PAYMENT_PENDING = 0
    const val PAYMENT_DONE = 1

    const val CHANGE_BASE_PRICE = 0
    const val CHANGE_MATERIAL_PRICE = 1
    const val CHANGE_DELIVERY_PRICE = 2

    // Permission Codes
    const val CHOOSE_IMAGE_REQ_CODE = 101
    const val READ_EXTERNAL_STORAGE_REQ_CODE = 102
    const val CHOOSE_FONT_REQ_CODE = 103
    const val AUTH_CREDENTIALS_REQ_CODE = 104

    // Auth
    const val DATASTORE_EMAIL = "emailId"
    const val LOCKED_FOR = "isLocked"

}