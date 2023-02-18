package com.radzhab.bulletinboard.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.radzhab.bulletinboard.databinding.ActivityEditAdsBinding

class EditAdsActivity : AppCompatActivity() {
    private lateinit var rootElement: ActivityEditAdsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
    }
}