package com.radzhab.bulletinboard.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object ImageManager {

    private const val MAX_IMAGE_SIZE = 1000
    const val WIDTH = 0
    const val HEIGHT = 1

    fun getImageSize(context: Context, uri: Uri): List<Int> {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
        return listOf(options.outWidth, options.outHeight)
    }


    suspend fun imageResize(context: Context, uris: List<Uri>): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val tempList = arrayListOf<List<Int>>()
            val bitmapList = ArrayList<Bitmap>()
            for (n in uris.indices) {
                val size = getImageSize(context, uris[n])
                val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()
                if (imageRatio > 1) {
                    if (size[WIDTH] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                    } else {
                        tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                    }
                } else {
                    if (size[HEIGHT] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                    } else {
                        tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                    }
                }
            }
            for (i in uris.indices) {
                val e = kotlin.runCatching {
                    bitmapList.add(
                        Picasso.get().load(uris[i])
                            .resize(tempList[i][WIDTH], tempList[i][HEIGHT]).get()
                    )
                }
                println(e)
            }
            return@withContext bitmapList
        }

    fun chooseScaleType(im: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) im.scaleType = ImageView.ScaleType.CENTER_CROP
        else im.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

}