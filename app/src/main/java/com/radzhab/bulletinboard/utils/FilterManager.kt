package com.radzhab.bulletinboard.utils

import com.radzhab.bulletinboard.model.Ad
import com.radzhab.bulletinboard.model.AdFilter

object FilterManager {
    fun createFilter(ad: Ad): AdFilter {
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

    fun getFilter(filter: String): String {
        val stringBuilderNode = StringBuilder()
        val stringBuilderFilter = StringBuilder()
        val tempArray = filter.split("_")
        if (tempArray[0] != "empty") {
            stringBuilderNode.append("country_")
            stringBuilderFilter.append("${tempArray[0]}_")
        }
        if (tempArray[1] != "empty") {
            stringBuilderNode.append("city_")
            stringBuilderFilter.append("${tempArray[1]}_")
        }
        if (tempArray[2] != "empty") {
            stringBuilderNode.append("index_")
            stringBuilderFilter.append("${tempArray[2]}_")
        }
        stringBuilderNode.append("withSent_time")
        stringBuilderFilter.append(tempArray[3])
        return "$stringBuilderNode|$stringBuilderFilter"
    }
}