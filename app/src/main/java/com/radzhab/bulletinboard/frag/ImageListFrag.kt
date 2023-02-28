package com.radzhab.bulletinboard.frag

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.databinding.ListImageFragBinding
import com.radzhab.bulletinboard.utils.ImageManager
import com.radzhab.bulletinboard.utils.ImagePicker
import com.radzhab.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(

    private val fragCloseInterface: FragmentCloseInterface,
    private val newUris: ArrayList<Uri>
) : Fragment() {
    lateinit var rootElement: ListImageFragBinding
    val adapter = SelectImageRvAdapter()
    private val drugCallback = ItemTouchMoveCallback(adapter)
    private lateinit var job: Job
    val touchHelper = ItemTouchHelper(drugCallback)

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
        job = CoroutineScope(Dispatchers.Main).launch {
            val text = ImageManager.imageResize(activity as EditAdsActivity, newUris)
            Log.d("MyLog", "result text at coroutine $text")
        }
        //        adapter.updateAdapter(newList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job.cancel()
    }

    private fun setUpToolbar() {
        rootElement.tbSelectedImages.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = rootElement.tbSelectedImages.menu.findItem(R.id.id_delete_image)
        val addImageItem = rootElement.tbSelectedImages.menu.findItem(R.id.id_add_image)

        rootElement.tbSelectedImages.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            true
        }

        addImageItem.setOnMenuItemClickListener {
            if (adapter.mainArray.size >= ImagePicker.MAX_IMAGE_COUNT) {
                Toast.makeText(context, getString(R.string.max_pics_count), Toast.LENGTH_LONG)
                    .show()
                return@setOnMenuItemClickListener false
            }
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.launcher(activity as EditAdsActivity, imageCount, false)
            true
        }

    }

    fun updateAdapter(newList: ArrayList<String>) {
        adapter.updateAdapter(newList, false)
    }

    fun setSingleImage(uri: String, pos : Int) {
        adapter.mainArray[pos] = uri
        adapter.notifyDataSetChanged()
    }

    fun imageResize(){

    }
}