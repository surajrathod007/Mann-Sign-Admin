package com.surajmanshal.mannsignadmin.data.model

data class Product(
    val productId: Int,
    var images: List<Image>? = null,
    var sizes: List<Size>? = null,
    var materials: List<Int>? = null,
    var languages: List<Int>? = null,
    var basePrice: Float? = null,
    var typeId: Int? = null,
    var posterDetails: Poster? = null,
    var category: Int? = null,
    var subCategory: Int? = null,
    var boardDetails: ACPBoard? = null,
    var bannerDetails: Banner? = null,
    )
