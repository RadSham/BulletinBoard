package com.radzhab.bulletinboard.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            dialog.showSpinnerDialog(this, listCountries)
        }
    }

    private fun init() {

    }
}