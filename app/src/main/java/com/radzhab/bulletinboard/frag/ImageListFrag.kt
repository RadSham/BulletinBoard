package com.radzhab.bulletinboard.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
) : Fragment(), AdapterCallback {
    lateinit var rootElement: ListImageFragBinding
    val adapter = SelectImageRvAdapter(this)
    private val drugCallback = ItemTouchMoveCallback(adapter)
    private var job: Job? = null
    val touchHelper = ItemTouchHelper(drugCallback)
    private var addImageItem:MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootElement = ListImageFragBinding.inflate(inflater)
        return rootElement.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        touchHelper.attachToRecyclerView(rootElement.rcViewSelectImage)
        rootElement.rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
        rootElement.rcViewSelectImage.adapter = adapter
        if (newUris != null) resizeSelectedImages(newUris, true)
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
        rootElement.tbSelectedImages.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = rootElement.tbSelectedImages.menu.findItem(R.id.id_delete_image)
        addImageItem = rootElement.tbSelectedImages.menu.findItem(R.id.id_add_image)

        rootElement.tbSelectedImages.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
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