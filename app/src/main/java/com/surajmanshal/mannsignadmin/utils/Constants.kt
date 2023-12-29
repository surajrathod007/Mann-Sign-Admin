package com.surajmanshal.mannsignadmin.utils

object Constants {


    const val TYPE_POSTER = 1
    const val TYPE_BANNER = 2
    const val TYPE_ACP_BOARD = 3

    val TYPE_ALL = listOf(TYPE_POSTER, TYPE_BANNER, TYPE_ACP_BOARD)

    const val ORDER_PENDING = 0
    const val ORDER_CONFIRMED = 1
    const val ORDER_PROCCESSING = 2
    const val ORDER_READY = 3
    const val ORDER_OUT_FOR_DELIVERY = 4
    const val ORDER_DELIVERED = 5
    const val ORDER_CANCELED = -1

    const val PAYMENT_PENDING = 0
    const val PAYMENT_DONE = 1

    const val CHANGE_BASE_PRICE = 0
    const val CHANGE_MATERIAL_PRICE = 1
    const val CHANGE_DELIVERY_PRICE = 2

    // Permission Codes
    const val CHOOSE_PROFILE_IMAGE = 101
    const val CHOOSE_PRODUCT_IMAGE = 104
    const val READ_EXTERNAL_STORAGE = 102
    const val CHOOSE_IMAGE_REQ_CODE = 101
    const val INSERT_IMAGE_REQ_CODE = 1000
    const val UPSERT_IMAGE_REQ_CODE = 1500
    const val UPDATE_IMAGE_REQ_CODE = 2000
    const val READ_EXTERNAL_STORAGE_REQ_CODE = 102
    const val CHOOSE_FONT_REQ_CODE = 103
    const val AUTH_CREDENTIALS_REQ_CODE = 104

    // Auth
    const val DATASTORE_EMAIL = "emailId"
    const val LOCKED_FOR = "isLocked"

    //get shared of count
    const val ORDER_COUNTS_PREF = "orderCountsPref"
    const val ORDER_COUNT = "orderCount"

    // Payment
    const val MERCHENT_ID = "GEHAnc22595306265338"
    const val MERCHENT_KEY = "Df52XjAjwekpgUHM"

    // Category Codes
    const val CATEGORY_CUSTOM_PRODUCT = 100
    const val CATEGORY_CUSTOM_POSTER = 101
    const val CATEGORY_CUSTOM_BANNER = 102
    const val CATEGORY_CUSTOM_BOARD = 103

    // Intent Extras
    const val PRODUCT = "product"
    const val NO_EMAIL = "no email"

    // Policy URLS
    const val URL_PRIVACY_POLICY = "https://manshal-git.github.io/MannSign/policy/privacy%20policy.html"
    const val URL_TERMS_OF_SERVICE = "https://manshal-git.github.io/MannSign/policy/terms%20of%20service.html"
    const val MANN_SIGN_PHONE_NUMBER = "+917621882363"
    const val APP_URL: String = "https://mannsign.com"

}