package com.radzhab.bulletinboard.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

    private val fragCloseInterface: FragmentCloseInterface,
    private val newUris: List<Uri>?
) : BaseAdsFrag(), AdapterCallback {
    val adapter = SelectImageRvAdapter(this)
    private val drugCallback = ItemTouchMoveCallback(adapter)
    private var job: Job? = null
    val touchHelper = ItemTouchHelper(drugCallback)
    private var addImageItem:MenuItem? = null
    lateinit var binding: ListImageFragBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            if (newUris != null) resizeSelectedImages(newUris, true)
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
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)
            ?.commit()
    }

    private fun resizeSelectedImages(list: List<Uri>, needClear: Boolean) {

        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(activity as EditAdsActivity, list)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size >= ImagePicker.MAX_IMAGE_COUNT) {
                addImageItem?.isVisible = false
            }
        }
    }

    private fun setUpToolbar() {

        binding.apply {
            tbSelectedImages.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = tbSelectedImages.menu.findItem(R.id.id_delete_image)
            addImageItem = tbSelectedImages.menu.findItem(R.id.id_add_image)

            tbSelectedImages.setNavigationOnClickListener {
                showInterAd()
            }



            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                true
            }

            addImageItem?.setOnMenuItemClickListener {
                /*if (adapter.mainArray.size >= ImagePicker.MAX_IMAGE_COUNT) {
                Toast.makeText(context, getString(R.string.max_pics_count), Toast.LENGTH_LONG)
                    .show()
                return@setOnMenuItemClickListener false
            }*/
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                ImagePicker.launcher(activity as EditAdsActivity, imageCount, false)
                true
            }
        }

    }

    fun updateAdapter(listUris: List<Uri>) {
        resizeSelectedImages(listUris, false)
    }
    fun setSingleImage(uri: Uri, pos: Int) {
//        val pBar = this.rootElement.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
//            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(activity as EditAdsActivity, listOf(uri))
//            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
        }

    }

    fun imageResize() {

    }
}