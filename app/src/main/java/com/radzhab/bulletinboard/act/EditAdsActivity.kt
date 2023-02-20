package com.radzhab.bulletinboard.act

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.databinding.ActivityEditAdsBinding
import com.radzhab.bulletinboard.dialogs.DialogSpinnerHelper
import com.radzhab.bulletinboard.utils.CityHelper

class EditAdsActivity : AppCompatActivity() {
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        //onClick tvSelectCountry
        rootElement.tvSelectCountry.setOnClickListener {
            val listCountries = CityHelper.getAllCountries(this)
            dialog.showSpinnerDialog(this, listCountries, rootElement.tvSelectCountry)
            if (rootElement.tvSelectCity.text.toString() != getString(R.string.select_city)) {
                rootElement.tvSelectCity.text = getString(R.string.select_city)
            }
        }
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
    }

    private fun init() {

    }
}