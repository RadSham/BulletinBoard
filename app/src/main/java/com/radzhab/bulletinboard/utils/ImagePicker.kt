package com.radzhab.bulletinboard.utils

import android.util.Log
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.frag.ImageListFrag
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options

object ImagePicker {
    lateinit var resultList: ArrayList<String>

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
//        launcher: ActivityResultLauncher<Intent>?,
        imageCounter: Int
    ) {
        edAct.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val fList = edAct.supportFragmentManager.fragments
                    fList.forEach {
                        if (it.isVisible) edAct.supportFragmentManager.beginTransaction().remove(it)
                            .commit()
                    }
                    val fm = edAct.supportFragmentManager.beginTransaction()
                    fm.replace(R.id.placeholder, ImageListFrag(edAct,
                        result.data.map { it.toString() } as ArrayList<String>))
                    fm.commit()
                    Log.d("MyLog", "result.data :${result.data}")
                }
                //use results as it.data
                PixEventCallback.Status.BACK_PRESSED -> Log.d(
                    "MyLog",
                    "BACK_PRESSED"
                ) // back pressed called
            }

        }
    }


}