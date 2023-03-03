package com.radzhab.bulletinboard.frag

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.databinding.SelectImageFragItemBinding
import com.radzhab.bulletinboard.utils.AdapterCallback
import com.radzhab.bulletinboard.utils.ImageManager
import com.radzhab.bulletinboard.utils.ImagePicker
import com.radzhab.bulletinboard.utils.ItemTouchMoveCallback

class SelectImageRvAdapter(val adapterCallback : AdapterCallback) : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {

    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding = SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(viewBinding, parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        mainArray[startPos] = targetItem
    }

    override fun onClear() {
        notifyDataSetChanged()
    }


    class ImageHolder(private val viewBinding: SelectImageFragItemBinding, val context: Context, val adapter: SelectImageRvAdapter) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun setData(bitmap: Bitmap) {

            viewBinding.imEditImage.setOnClickListener {
                viewBinding.pBar.visibility = View.VISIBLE
                ImagePicker.launcher(context as EditAdsActivity, 1, true)
                context.editImagePosition = adapterPosition
            }

            viewBinding.imDeleteImage.setOnClickListener {
                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
//                adapter.notifyDataSetChanged()
                for (n in 0 until adapter.mainArray.size)
                    adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()
            }
            viewBinding.tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(viewBinding.imageContent, bitmap)
            viewBinding.imageContent.setImageBitmap(bitmap)        }
    }

    fun updateAdapter(newList: List<Bitmap>, needClear: Boolean) {
        if (needClear) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }


}