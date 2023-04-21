package com.radzhab.bulletinboard.adaptors

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.databinding.AdListItemBinding
import com.radzhab.bulletinboard.model.Ad
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class AdsRcAdapter(val act: MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()
    var timeFormatter:SimpleDateFormat? = null

    init{
        timeFormatter = SimpleDateFormat("dd/MM/yyyy - hh:mm", Locale.getDefault())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, act, timeFormatter)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(newList: List<Ad>) {
        val tempArray = checkIfFavsChanged(adArray, newList)
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(tempArray)
    }

    fun checkIfFavsChanged(list: List<Ad>, list2: List<Ad>): MutableList<Ad> {
        val tempArray = ArrayList<Ad>()
        tempArray.addAll(list)
        loop@ for (ad2 in list2) {
            for (ad in list)
                if (ad.key == ad2.key && ad.isFav != ad2.isFav) {
                    tempArray[tempArray.indexOf(ad)] = ad2
                    continue@loop
                }
            if (!tempArray.contains(ad2)) tempArray.add(ad2)
        }
        return tempArray
    }

    fun updateAdapterWithClear(newList: List<Ad>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, newList))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(newList)
    }

    class AdHolder(
        val binding: AdListItemBinding,
        val act: MainActivity,
        val timeFormatter: SimpleDateFormat?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvTitle.text = ad.title
            tvPrice.text = ad.price
            tvViewCounter.text = ad.viewsCounter
            tvFavCounter.text = ad.favCounter
            val publishTime = act.getString(R.string.publish_time) + getTimeFromMillis(ad.time)
            tvPublishtime.text = publishTime

            Picasso.get().load(ad.mainImage).into(mainImage)

            isFav(ad)
            showEditPanel(isOwner(ad))
            mainOnClick(ad)
        }
        private fun getTimeFromMillis(timeMillis:String) : String{
            val c = Calendar.getInstance()
            c.timeInMillis = timeMillis.toLong()
            return timeFormatter!!.format(c.time)
        }

        private fun mainOnClick(ad: Ad) = with(binding) {
            ibFav.setOnClickListener {
                if (act.myAuth.currentUser?.isAnonymous == false) act.onFavClicked(ad)
            }
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener {
                act.onDeleteItem(ad)
            }
        }

        private fun isFav(ad: Ad) {
            if (ad.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_fav_not_pressed)
            }
        }

        private fun onClickEdit(ad: Ad): OnClickListener {
            return OnClickListener {
                val editIntent = Intent(act, EditAdsActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean {
            return ad.uid == act.myAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }

    }

    interface Listener {
        fun onDeleteItem(ad: Ad)
        fun onAdViewed(ad: Ad)
        fun onFavClicked(ad: Ad)
    }


}