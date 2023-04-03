package com.radzhab.bulletinboard.act

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.databinding.ActivityFilterBinding
import com.radzhab.bulletinboard.dialogs.DialogSpinnerHelper
import com.radzhab.bulletinboard.utils.CityHelper

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickSelectCity()
        onClickSelectCountry()
        onClickButtonFilterApply()
        actionBarSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    //onClick tvSelectCity
    private fun onClickSelectCity() = with(binding) {
        tvSelectCity.setOnClickListener {
            val selectedCountry = tvSelectCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCities = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCities, binding.tvSelectCity)
            } else {
                Toast.makeText(
                    this@FilterActivity,
                    getString(R.string.no_country_selected),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    //onClick tvSelectCountry
    private fun onClickSelectCountry() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountries = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountries, binding.tvSelectCountry)
            if (tvSelectCity.text.toString() != getString(R.string.select_city)) {
                tvSelectCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickButtonFilterApply() = with(binding) {
        btFilterApply.setOnClickListener {
            Log.d("MyLog", "Filter: ${createFilter()}")
        }
    }

    private fun createFilter(): String = with(binding) {
        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            tvSelectCountry.text,
            tvSelectCity.text,
            edIndex.text,
            checkBoxWithSend.isChecked.toString()
        )
        for ((i, s) in arrayTempFilter.withIndex()) {
            if (s != getString(R.string.select_country) &&
                s != getString(R.string.select_city) &&
                s.isNotEmpty()
            ) {
                stringBuilder.append(s)
                if (i != arrayTempFilter.size - 1) stringBuilder.append("_")
            }
        }
        return stringBuilder.toString()
    }

    fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }


}