package com.radzhab.bulletinboard.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface


object ImageManager {

    const val MAX_IMAGE_SIZE = 1000
    const val WIDTH = 0
    const val HEIGHT = 1

    fun getImageSize(context: Context, uri: Uri): List<Int> {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
        return if (imageRotation(context, uri) == 90)
            listOf(options.outHeight, options.outWidth)
        else
            listOf(options.outWidth, options.outHeight)
    }

    //TODO: doesn't work
    fun imageRotation(context: Context, uri: Uri): Int {
        val rotation: Int

        val inputStream = context.contentResolver.openInputStream(uri)

        val exif = inputStream?.let { ExifInterface(it) }
        val orientation =
            exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        Log.d("MyLog", "imageRotation $orientation")
        rotation =
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270)
                90
            else 0
        inputStream?.close()
        return rotation
    }

    fun imageResize(context: Context, uris: List<Uri>) : ArrayList<List<Int>> {
        val tempList = arrayListOf<List<Int>>()
        for (n in uris.indices) {
            val size = getImageSize(context, uris[n])
            Log.d("MyLog", "size w ${size[WIDTH]} h ${size[HEIGHT]}")

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
            Log.d("MyLog", "compressed size w ${tempList[n][WIDTH]} h ${tempList[n][HEIGHT]}")
        }
        return tempList
    }

}