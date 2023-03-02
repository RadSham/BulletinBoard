package com.radzhab.bulletinboard.dialogHelper

import android.app.Activity
import android.app.AlertDialog
import com.radzhab.bulletinboard.databinding.ProgresDailogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(activity: Activity) :AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = ProgresDailogLayoutBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}