package com.radzhab.bulletinboard.act

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
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
import com.radzhab.bulletinboard.utils.ImageManager
import com.radzhab.bulletinboard.utils.ImagePicker
import java.io.ByteArrayOutputStream
import java.util.*

class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
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
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        onClickSelectCountry()
        onClickSelectCity()
        onClickSelectCategory()
        onClickGetImage()
        onClickButtonPublish()
        init()
        checkEditState()
        imageChangeCounter()
    }

    //onClick tvSelectCountry
    private fun onClickSelectCountry() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountries = CityHelper.getAllCountries(this@EditAdsActivity)
            dialog.showSpinnerDialog(this@EditAdsActivity, listCountries, binding.tvSelectCountry)
            if (tvSelectCity.text.toString() != getString(R.string.select_city)) {
                tvSelectCity.text = getString(R.string.select_city)
            }
        }
    }

    //onClick tvSelectCity
    private fun onClickSelectCity() = with(binding) {
        tvSelectCity.setOnClickListener {
            val selectedCountry = tvSelectCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCities = CityHelper.getAllCities(selectedCountry, this@EditAdsActivity)
                dialog.showSpinnerDialog(this@EditAdsActivity, listCities, binding.tvSelectCity)
            } else {
                Toast.makeText(
                    this@EditAdsActivity,
                    getString(R.string.no_country_selected),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    //onClick tvSelectCategory
    private fun onClickSelectCategory() = with(binding) {
        tvSelectCategory.setOnClickListener {
            val listCategories =
                resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this@EditAdsActivity, listCategories, binding.tvSelectCategory)
        }
    }

    //onClick GetImage
    private fun onClickGetImage() = with(binding) {
        btGetImage.setOnClickListener {
            if (imageAdapter.mainArray.size < 1) {
                //before android 13
//                ImagePicker.getMultiImages(this@EditAdsActivity, ImagePicker.MAX_IMAGE_COUNT)
                launchPickerMultipleMode()
                ImagePicker.closePicsFragment(this@EditAdsActivity)
            } else {
                openChooseImageFrag(null)
                chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
            }
        }
    }

    private fun onClickButtonPublish() = with(binding) {
        btPublish.setOnClickListener {
            ad = fillAd()
            uploadImages()
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
        binding.apply {
            adTemp = Ad(
                tvSelectCountry.text.toString(),
                tvSelectCity.text.toString(),
                edTelephone.text.toString(),
                edEmail.text.toString(),
                edIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                tvSelectCategory.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                ad?.mainImage ?: "empty",
                ad?.secondImage ?: "empty",
                ad?.thirdImage ?: "empty",
                ad?.key ?: dbManager.db.push().key.toString(),
                dbManager.auth.uid,
                ad?.time ?: System.currentTimeMillis().toString()
            )
        }
        return adTemp
    }

    private fun fillViews(ad: Ad) = with(binding) {
        tvSelectCountry.text = ad.country
        tvSelectCity.text = ad.city
        edTelephone.setText(ad.telephone)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSent.toBoolean()
        tvSelectCategory.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
        updateImageCounter(0)
        ImageManager.fillImageArray(ad, imageAdapter)

    }

    @Suppress("DEPRECATION")
    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState()) {
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if (ad != null) fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
    }

    override fun onFragClose(list: List<Bitmap>) {
        binding.scrollViewMine.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
        updateImageCounter(binding.vpImages.currentItem)
    }

    fun openChooseImageFrag(newList: List<Uri>?) {
        chooseImageFrag = ImageListFrag(this)
        if (newList != null) chooseImageFrag?.resizeSelectedImages(newList, true, this)
        binding.scrollViewMine.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeholder, chooseImageFrag!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageIndex == 3) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val urlOld = getUrlFromAd()
        if (imageAdapter.mainArray.size > imageIndex) {
            val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
            if (urlOld.startsWith("http")) {
                updateImage(byteArray, urlOld) {
                    nextImage(it.result.toString())
                }
            } else {
                uploadImage(byteArray) {
                    nextImage(it.result.toString())
                }
            }
        } else {
            if (urlOld.startsWith("http")) {
                deleteImageByUrl(urlOld) {
                    nextImage("empty")
                }
            } else {
                nextImage("empty")
            }
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

    private fun getUrlFromAd(): String {
        return listOf(ad?.mainImage!!, ad?.secondImage!!, ad?.thirdImage!!)[imageIndex]
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
        upTask.continueWithTask {
            imStorageReference.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun deleteImageByUrl(oldUrl: String, listener: OnCompleteListener<Void>) {
        dbManager.dbStorage.storage
            .getReferenceFromUrl(oldUrl)
            .delete().addOnCompleteListener(listener)

    }

    private fun updateImage(byteArray: ByteArray, url: String, listener: OnCompleteListener<Uri>) {
        val imStorageReference = dbManager.dbStorage.storage.getReferenceFromUrl(url)
        val upTask = imStorageReference.putBytes(byteArray)
        upTask.continueWithTask {
            imStorageReference.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter() {
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    private fun updateImageCounter(counter: Int) {
        var index = 1
        val itemCount = binding.vpImages.adapter?.itemCount
        if (itemCount == 0) index = 0
        val imageCounter = "${counter + index}/${binding.vpImages.adapter?.itemCount}"
        binding.tvImageCounterEa.text = imageCounter
    }

    //upload single picture
    fun launchPickerSingleMode() {
        val m = ActivityResultContracts.PickVisualMedia.ImageOnly
        try {
            startForSingleModeResult.launch(
                PickVisualMediaRequest.Builder().setMediaType(m).build()
            )
        } catch (ex: ActivityNotFoundException) {
            showToast(ex.localizedMessage ?: "error")
        }
    }

    private val startForSingleModeResult =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { currentUri ->
            if (currentUri != null) {
                showToast("Selected URI: $currentUri")
                ImagePicker.openChooseImageFrag(this)
                ImagePicker.singleImage(this, currentUri)
            } else {
                showToast("No media selected")
            }
        }

    //upload multiple pictures
    private fun launchPickerMultipleMode() {
        try {
            startForMultipleModeResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (ex: ActivityNotFoundException) {
            showToast(ex.localizedMessage ?: "error")
        }
    }

    private val startForMultipleModeResult =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(ImagePicker.MAX_IMAGE_COUNT)
        ) { uris ->
            if (uris.isNotEmpty()) {
                ImagePicker.getMultiSelectedImages(this, uris)
            } else {
                showToast("No media selected")
            }
        }


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}