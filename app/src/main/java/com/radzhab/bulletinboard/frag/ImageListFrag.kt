package com.radzhab.bulletinboard.frag

import android.app.Activity
import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.radzhab.bulletinboard.Constants.TAG_LOG
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.databinding.ListImageFragBinding
import com.radzhab.bulletinboard.dialogHelper.ProgressDialog
import com.radzhab.bulletinboard.utils.AdapterCallback
import com.radzhab.bulletinboard.utils.ImageManager
import com.radzhab.bulletinboard.utils.ImagePicker
import com.radzhab.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(

    private val fragCloseInterface: FragmentCloseInterface
) : BaseAdsFrag(), AdapterCallback {
    val adapter = SelectImageRvAdapter(this)
    private val drugCallback = ItemTouchMoveCallback(adapter)
    private var job: Job? = null
    val touchHelper = ItemTouchHelper(drugCallback)
    private var addImageItem: MenuItem? = null
    lateinit var binding: ListImageFragBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListImageFragBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        binding.apply {
            touchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter
        }
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()

    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)
            ?.commit()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    fun resizeSelectedImages(list: List<Uri>, needClear: Boolean, activity: Activity) {

        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity)
            val bitmapList = ImageManager.imageResize(activity as EditAdsActivity, list)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }

    private fun setUpToolbar() {
        binding.apply {
            tbSelectedImages.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = tbSelectedImages.menu.findItem(R.id.id_delete_image)
            addImageItem = tbSelectedImages.menu.findItem(R.id.id_add_image)
            if (adapter.mainArray.size > 2) addImageItem?.isVisible = false

            tbSelectedImages.setNavigationOnClickListener {
                showInterAd()
            }

            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                true
            }

            addImageItem?.setOnMenuItemClickListener {
                //before android 13
//                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
//                ImagePicker.addImages(activity as EditAdsActivity, imageCount)
                launchPickerAddSingleMode(activity as EditAdsActivity)
                true
            }
        }
    }

    private fun updateAdapter(listUris: List<Uri>, activity: Activity) {
        resizeSelectedImages(listUris, false, activity)
    }

    fun setSingleImage(uri: Uri, pos: Int) {
        val pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(activity as EditAdsActivity, arrayListOf(uri))
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
        }

    }

    fun imageResize() {

    }

    //add multiple pictures
    private fun launchPickerAddSingleMode(edAct: EditAdsActivity) {
        try {
            startForAddSingleModeResult.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        } catch (ex: ActivityNotFoundException) {
            edAct.showToast(ex.localizedMessage ?: "error")
        }
    }

    private val startForAddSingleModeResult =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { currentUri ->
            if (currentUri != null) {
                Log.d(TAG_LOG, "Number of items selected: currentUri")
                // output log.
                ImagePicker.openChooseImageFrag(activity as EditAdsActivity)
                (activity as EditAdsActivity).chooseImageFrag?.updateAdapter(
                    listOf(currentUri),
                    (activity as EditAdsActivity)
                )
            } else {
                Log.d(TAG_LOG, "No media selected")
            }
        }
}
