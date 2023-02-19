package com.radzhab.bulletinboard.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.radzhab.bulletinboard.databinding.ActivityEditAdsBinding
import com.radzhab.bulletinboard.dialogs.DialogSpinnerHelper
import com.radzhab.bulletinboard.utils.CityHelper

class EditAdsActivity : AppCompatActivity() {
    private lateinit var rootElement: ActivityEditAdsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        val listCountries = CityHelper.getAllCountries(this)

        val dialog = DialogSpinnerHelper()

        dialog.showSpinnerDialog(this, listCountries)
    }
}