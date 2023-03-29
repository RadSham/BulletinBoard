package com.radzhab.bulletinboard.utils

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3

    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "Pix/Camera"
        }
        return options
    }

    fun getMultiImages(edAct: EditAdsActivity, imageCounter: Int) {
        edAct.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectedImages(edAct, result.data)
                    closePicsFragment(edAct)
                }
                //use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> Log.d(
                    "MyLog",
                    "BACK_PRESSED"
                )
            }
        }

    }

    fun addImages(edAct: EditAdsActivity, imageCounter: Int) {
        edAct.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                    edAct.chooseImageFrag?.updateAdapter(result.data, edAct)
                }
                //use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> Log.d(
                    "MyLog",
                    "BACK_PRESSED"
                )
            }
        }
    }

    fun getSingleImages(edAct: EditAdsActivity) {
        edAct.addPixToActivity(R.id.placeholder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                    singleImage(edAct, result.data[0])
                }

                //use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> Log.d(
                    "MyLog",
                    "BACK_PRESSED"
                )
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsActivity) {
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        edAct.supportFragmentManager.beginTransaction().replace(R.id.placeholder, edAct.chooseImageFrag!!).commit()
    }

    fun getMultiSelectedImages(edAct: EditAdsActivity, uris: List<Uri>) {
        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseImageFrag(uris)
        } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.rootElement.pBarLoad.visibility = View.VISIBLE
                val bitMapArray = ImageManager.imageResize(edAct, uris) as ArrayList<Bitmap>
                edAct.rootElement.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.update(bitMapArray)
            }
        }
    }

    private fun singleImage(edAct: EditAdsActivity, uri: Uri) {
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePosition)
    }

    private fun closePicsFragment(edAct: EditAdsActivity) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) {
                edAct.supportFragmentManager.beginTransaction().remove(it)
                    .commit()
            }
        }
    }
/*
    private fun updateChooseImageFrag(edAct: EditAdsActivity, newList: List<Uri>) {
        edAct.chooseImageFrag = ImageListFrag(edAct)
        edAct.chooseImageFrag?.updateAdapterFromEdit(edAct.imageAdapter.mainArray)
        edAct.chooseImageFrag!!.updateAdapter(newList, edAct)
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        val fm = edAct.supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, edAct.chooseImageFrag!!)
        fm.commit()
    }

    private fun updateOneImageFrag(edAct: EditAdsActivity, newImage: Uri) {
        edAct.chooseImageFrag = ImageListFrag(edAct)
        edAct.chooseImageFrag?.updateAdapterFromEdit(edAct.imageAdapter.mainArray)
        edAct.chooseImageFrag!!.setSingleImage(newImage, edAct.editImagePosition)
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        val fm = edAct.supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, edAct.chooseImageFrag!!)
        fm.commit()
    }*/

}