package com.radzhab.bulletinboard.adaptors

import androidx.recyclerview.widget.DiffUtil
import com.radzhab.bulletinboard.model.Ad

class DiffUtilHelper(val oldList: List<Ad>, val newList: List<Ad>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}