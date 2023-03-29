package com.radzhab.bulletinboard.model

data class Ad(
    val country: String? = null,
    val city: String? = null,
    val telephone: String? = null,
    val index: String? = null,
    val withSent: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val key: String? = null,
    val uid: String? = null,
    var favCounter: String = "0",
    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailCounter: String = "0",
    var callsCounter: String = "0"
) : java.io.Serializable