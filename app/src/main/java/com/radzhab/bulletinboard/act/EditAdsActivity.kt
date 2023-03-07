package com.radzhab.bulletinboard.act

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.adaptors.ImageAdapter
import com.radzhab.bulletinboard.databinding.ActivityEditAdsBinding
import com.radzhab.bulletinboard.dialogs.DialogSpinnerHelper
import com.radzhab.bulletinboard.frag.FragmentCloseInterface
import com.radzhab.bulletinboard.frag.ImageListFrag
import com.radzhab.bulletinboard.utils.CityHelper
import com.radzhab.bulletinboard.utils.ImagePicker

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {
    lateinit var rootElement: ActivityEditAdsBinding
    val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var chooseImageFrag: ImageListFrag? = null
    var editImagePosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        //onClick tvSelectCountry
        rootElement.tvSelectCountry.setOnClickListener {
            val listCountries = CityHelper.getAllCountries(this)
            dialog.showSpinnerDialog(this, listCountries, rootElement.tvSelectCountry)
            if (rootElement.tvSelectCity.text.toString() != getString(R.string.select_city)) {
                rootElement.tvSelectCity.text = getString(R.string.select_city)
            }
        }
        //onClick tvSelectCity
        rootElement.tvSelectCity.setOnClickListener {
            val selectedCountry = rootElement.tvSelectCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCities = CityHelper.getAllCities(selectedCountry, this)
                dialog.showSpinnerDialog(this, listCities, rootElement.tvSelectCity)
            } else {
                Toast.makeText(this, getString(R.string.no_country_selected), Toast.LENGTH_LONG)
                    .show()
            }
        }
        //onClick tvSelectCategory
        rootElement.tvSelectCategory.setOnClickListener {
                val listCategories = resources.getStringArray(R.array.category).toMutableList() as ArrayList
                dialog.showSpinnerDialog(this, listCategories, rootElement.tvSelectCategory)

        }
        //onClick GetImage
        rootElement.btGetImage.setOnClickListener {
            if (imageAdapter.mainArray.size < 1) {
                ImagePicker.launcher(this, ImagePicker.MAX_IMAGE_COUNT, false)
            } else {
                ImagePicker.openChooseImageFrag(this, null)
                chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
            }
            rootElement.scrollViewMine.visibility = View.GONE
        }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter
    }


    override fun onFragClose(list: List<Bitmap>) {
        rootElement.scrollViewMine.visibility = View.VISIBLE
        imageAdapter.update(list)
//        chooseImageFrag = null
    }

}