package com.radzhab.bulletinboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.radzhab.bulletinboard.model.Ad
import com.radzhab.bulletinboard.model.DbManager

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
    fun loadAllAdsFirstPage(filter: String) {
        dbManager.getAllAdsFirstPage(filter, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsNextPage(time: String, filter: String) {
        dbManager.getAllAdsNextPage(time, filter, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCatFirstPage(lastCatTime: String, filter: String) {
        dbManager.getAllAdsFromCatFirstPage(
            lastCatTime,
            filter,
            object : DbManager.ReadDataCallback {
                override fun readData(list: ArrayList<Ad>) {
                    liveAdsData.value = list
                }
            })
    }

    fun loadAllAdsFromCatNextPage(catTime: String) {
        dbManager.getAllAdsFromCatNextPage(catTime, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun onFavClick(ad: Ad) {
        dbManager.onFavClick(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)
                if (pos != -1) {
                    pos?.let {
                        val favCounter =
                            if (ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(
                            favCounter = favCounter.toString(),
                            isFav = !ad.isFav
                        )
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad: Ad) {
        dbManager.adViewed(ad)
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyFavs() {
        dbManager.getMyFavs(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteItem(ad: Ad) {
        dbManager.deleteAdd(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }
}