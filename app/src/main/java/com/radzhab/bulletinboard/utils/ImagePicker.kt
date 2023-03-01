package com.radzhab.bulletinboard.utils

import android.util.Log
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
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
                        edAct.openChooseImageFrag(result.data)
                    } else if (updateOneImage) {
                        edAct.updateOneImageFrag(result.data[0])
                    } else {
                        edAct.updateChooseImageFrag(result.data)
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

}