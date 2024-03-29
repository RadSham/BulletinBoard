package com.radzhab.bulletinboard.model

data class Ad(
    val country: String? = null,
    val city: String? = null,
    val telephone: String? = null,
    val email: String? = null,
    val index: String? = null,
    val withSent: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val secondImage: String? = null,
    val thirdImage: String? = null,
    val key: String? = null,
    val uid: String? = null,
    val time: String = "0",
    var favCounter: String = "0",
    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailCounter: String = "0",
    var callsCounter: String = "0"
) : java.io.Serializable