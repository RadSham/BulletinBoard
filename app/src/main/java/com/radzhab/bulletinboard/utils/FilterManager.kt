package com.radzhab.bulletinboard.utils

import com.radzhab.bulletinboard.model.Ad
import com.radzhab.bulletinboard.model.AdFilter

object FilterManager {
    fun createFilter(ad: Ad) : AdFilter{
        return AdFilter(
            ad.time,
            "${ad.category}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.category}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.withSent}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.index}_${ad.withSent}_${ad.time}",
            "${ad.withSent}_${ad.time}"

        )
    }
}