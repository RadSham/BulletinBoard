package com.radzhab.bulletinboard.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.adaptors.ImageAdapter
import com.radzhab.bulletinboard.databinding.ActivityDescriptionBinding
import com.radzhab.bulletinboard.model.Ad
import com.radzhab.bulletinboard.utils.ImageManager

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var imageAdapter: ImageAdapter
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fbTel.setOnClickListener { call() }
        binding.fbEmail.setOnClickListener { sendEmail() }
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = imageAdapter
        }
        getIntentFromMainActivity()
        imageChangeCounter()
    }

    @Suppress("DEPRECATION")
    private fun getIntentFromMainActivity() {
        ad = intent.getSerializableExtra(MainActivity.AD) as Ad
        if (ad != null) updateUI(ad!!)
    }

    private fun updateUI(ad: Ad) {
        ImageManager.fillImageArray(ad, imageAdapter)
        fillTestViews(ad)
    }


    private fun fillTestViews(ad: Ad) = with(binding) {
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvTelephone.text = ad.telephone
        tvEmail.text = ad.email
        tvIndex.text = ad.index
        tvWithSent.text = isWithSent(ad.withSent.toBoolean())
        tvPrice.text = ad.price
    }

    private fun isWithSent(withSent: Boolean): String {
        return if (withSent) "Да" else "Нет"
    }

    private fun call() {
        val callUri = "tel:${ad?.telephone}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail() {
        val iSentEmail = Intent(Intent.ACTION_SEND)
        iSentEmail.type = "message/rfc822"
        iSentEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Объявление")
            putExtra(Intent.EXTRA_TEXT, "Меня интересует ваше объявление")
        }
        try {
            startActivity(Intent.createChooser(iSentEmail, "Открыть с помощью"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "У вас нет приложения для отправки почты", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun imageChangeCounter() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    private fun updateImageCounter(counter: Int) {
        var index = 1
        val itemCount = binding.viewPager.adapter?.itemCount
        if (itemCount == 0) index = 0
        val imageCounter = "${counter + index}/$itemCount"
        binding.tvImageCounter.text = imageCounter
    }

}