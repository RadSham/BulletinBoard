package com.radzhab.bulletinboard.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.adaptors.ImageAdapter
import com.radzhab.bulletinboard.databinding.ActivityEditAdsBinding
import com.radzhab.bulletinboard.dialogs.DialogSpinnerHelper
import com.radzhab.bulletinboard.frag.FragmentCloseInterface
import com.radzhab.bulletinboard.frag.ImageListFrag
import com.radzhab.bulletinboard.model.Ad
import com.radzhab.bulletinboard.model.DbManager
import com.radzhab.bulletinboard.utils.CityHelper
import com.radzhab.bulletinboard.utils.ImagePicker
import java.io.ByteArrayOutputStream

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {
    lateinit var rootElement: ActivityEditAdsBinding
    val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var chooseImageFrag: ImageListFrag? = null
    private val dbManager = DbManager()

    var editImagePosition = 0
    private var imageIndex = 0
    private var isEditState: Boolean = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        checkEditState()

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
            val listCategories =
                resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this, listCategories, rootElement.tvSelectCategory)

        }
        //onClick GetImage
        rootElement.btGetImage.setOnClickListener {
            if (imageAdapter.mainArray.size < 1) {
                ImagePicker.getMultiImages(this, ImagePicker.MAX_IMAGE_COUNT)
            } else {
                openChooseImageFrag(null)
                chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
            }
        }

        rootElement.btPublish.setOnClickListener {
            ad = fillAd()
            if (isEditState) {
                ad?.copy(key = this.ad?.key)
                    ?.let { it1 -> dbManager.publishAd(it1, onPublishFinish()) }
            } else {
//                dbManager.publishAd(adTemp, onPublishFinish())
                uploadImages()
            }
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillAd(): Ad {
        val adTemp: Ad
        rootElement.apply {
            adTemp = Ad(
                tvSelectCountry.text.toString(),
                tvSelectCity.text.toString(),
                edTelephone.text.toString(),
                edIndex.text.toString(),
                checkBoxWithSend.toString(),
                tvSelectCategory.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                "empty",
                "empty",
                "empty",
                ad?.key ?: dbManager.db.push().key.toString(),
                dbManager.auth.uid
            )
        }

        return adTemp
    }

    private fun fillViews(ad: Ad) = with(rootElement) {
        tvSelectCountry.text = ad.country
        tvSelectCity.text = ad.city
        edTelephone.setText(ad.telephone)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSent.toBoolean()
        tvSelectCategory.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
    }

    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState()) {
            @Suppress("DEPRECATION")
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if (ad != null)
                fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter
    }

    override fun onFragClose(list: List<Bitmap>) {
        rootElement.scrollViewMine.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
    }

    fun openChooseImageFrag(newList: List<Uri>?) {
        chooseImageFrag = ImageListFrag(this)
        if (newList != null) chooseImageFrag?.resizeSelectedImages(newList, true, this)
        rootElement.scrollViewMine.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, chooseImageFrag!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageAdapter.mainArray.size == imageIndex) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
        uploadImage(byteArray) {
//            dbManager.publishAd(ad!!, onPublishFinish())
            nextImage(it.result.toString())
        }
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun setImageUriToAd(uri: String) {
        when (imageIndex) {
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(secondImage = uri)
            2 -> ad = ad?.copy(thirdImage = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imStorageReference = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val upTask = imStorageReference.putBytes(byteArray)
        upTask.continueWithTask { task ->
            imStorageReference.downloadUrl
        }.addOnCompleteListener(listener)
    }

}