package com.radzhab.bulletinboard.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3

// before android 13
/*    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "Pix/Camera"
        }
        return options
    }*/


    fun openChooseImageFrag(edAct: EditAdsActivity) {
        edAct.binding.scrollViewMine.visibility = View.GONE
        edAct.supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, edAct.chooseImageFrag!!).commit()
    }

    fun getMultiSelectedImages(edAct: EditAdsActivity, uris: List<Uri>) {
        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseImageFrag(uris)
        } else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.binding.pBarLoad.visibility = View.VISIBLE
                val bitMapArray = ImageManager.imageResize(edAct, uris) as ArrayList<Bitmap>
                edAct.binding.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.update(bitMapArray)
            }
        }
    }

    fun singleImage(edAct: EditAdsActivity, uri: Uri) {
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePosition)
    }

    fun closePicsFragment(edAct: EditAdsActivity) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) {
                edAct.supportFragmentManager.beginTransaction().remove(it)
                    .commit()
            }
        }
    }
}



//BEFORE ANDROID 13
/*   fun getMultiImages(edAct: EditAdsActivity, imageCounter: Int) {
       edAct.addPixToActivity(R.id.placeholder, getOptions(imageCounter)) { result ->
           when (result.status) {
               PixEventCallback.Status.SUCCESS -> {
                   getMultiSelectedImages(edAct, result.data)
                   closePicsFragment(edAct)
               }
               //use results as it.data
               PixEventCallback.Status.BACK_PRESSED -> Log.d(
                   TAG_LOG,
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
                TAG_LOG,
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
                TAG_LOG,
                "BACK_PRESSED"
            )
        }
    }
}

*/
