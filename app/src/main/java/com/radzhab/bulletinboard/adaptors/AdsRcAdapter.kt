package com.radzhab.bulletinboard.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.radzhab.bulletinboard.data.Ad
import com.radzhab.bulletinboard.databinding.AdListItemBinding

class AdsRcAdapter : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    class AdHolder(val binding: AdListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) {
            binding.apply {
                tvDescription.text = ad.description
                tvTitle.text = ad.title
                tvPrice.text = ad.price
            }
        }
    }

    fun update(newList: List<Ad>){
        adArray.clear()
        adArray.addAll(newList)
        notifyDataSetChanged()
    }
}