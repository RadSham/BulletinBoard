package com.radzhab.bulletinboard.database

import com.radzhab.bulletinboard.data.Ad

interface ReadDataCallback {
    fun readData(list:List<Ad>)
}