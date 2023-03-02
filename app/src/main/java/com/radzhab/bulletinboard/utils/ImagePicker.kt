package com.radzhab.bulletinboard.utils

import android.net.Uri
import android.util.Log
import android.view.View
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.frag.ImageListFrag
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options

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

    fun launcher(
        edAct: EditAdsActivity,
        imageCounter: Int,
        updateOneImage: Boolean
    ) {
        edAct.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val fList = edAct.supportFragmentManager.fragments
                    fList.forEach {
                        if (it.isVisible) {
                            edAct.supportFragmentManager.beginTransaction().remove(it)
                                .commit()
                        }
                    }
                    if (edAct.chooseImageFrag == null) {
                        openChooseImageFrag(edAct, result.data)
                    } else if (updateOneImage) {
                        updateOneImageFrag(edAct,result.data[0])
                    } else {
                        updateChooseImageFrag(edAct,result.data)
                    }
                }
                //use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> Log.d(
                    "MyLog",
                    "BACK_PRESSED"
                )
            }

        }
    }

    fun openChooseImageFrag(edAct: EditAdsActivity, newList: List<Uri>?) {
        edAct.chooseImageFrag = ImageListFrag(edAct, newList)
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        val fm = edAct.supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, edAct.chooseImageFrag!!)
        fm.commit()
    }

    private fun updateChooseImageFrag(edAct: EditAdsActivity, newList: List<Uri>) {
        edAct.chooseImageFrag = ImageListFrag(edAct, null)
        edAct.chooseImageFrag?.updateAdapterFromEdit(edAct.imageAdapter.mainArray)
        edAct.chooseImageFrag!!.updateAdapter(newList)
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        val fm = edAct.supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, edAct.chooseImageFrag!!)
        fm.commit()
    }

    private fun updateOneImageFrag(edAct: EditAdsActivity, newImage: Uri) {
        edAct.chooseImageFrag = ImageListFrag(edAct, null)
        edAct.chooseImageFrag?.updateAdapterFromEdit(edAct.imageAdapter.mainArray)
        edAct.chooseImageFrag!!.setSingleImage(newImage, edAct.editImagePosition)
        edAct.rootElement.scrollViewMine.visibility = View.GONE
        val fm = edAct.supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, edAct.chooseImageFrag!!)
        fm.commit()
    }

}