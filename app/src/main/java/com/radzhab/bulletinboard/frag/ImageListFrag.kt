package com.radzhab.bulletinboard.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.databinding.ListImageFragBinding
import com.radzhab.bulletinboard.utils.ImagePicker
import com.radzhab.bulletinboard.utils.ItemTouchMoveCallback

class ImageListFrag(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>
) : Fragment() {
    lateinit var rootElement: ListImageFragBinding
    val adapter = SelectImageRvAdapter()
    private val drugCallback = ItemTouchMoveCallback(adapter)
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
        val updateList = ArrayList<SelectImageItem>()
        for (n in 0 until newList.size)
            updateList.add(SelectImageItem(n.toString(), newList[n]))
        adapter.updateAdapter(updateList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
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
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.launcher(activity as EditAdsActivity, imageCount)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        val updateList = ArrayList<SelectImageItem>()
        for (n in adapter.mainArray.size until newList.size + adapter.mainArray.size) {
            updateList.add(SelectImageItem(n.toString(), newList[n-adapter.mainArray.size]))
        }
        adapter.updateAdapter(updateList, false)
    }
}