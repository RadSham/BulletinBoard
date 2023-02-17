package com.radzhab.bulletinboard.dialogHelper

import android.app.AlertDialog
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.accountHelper.AccountHelper
import com.radzhab.bulletinboard.databinding.SignDialogBinding
import java.nio.file.attribute.AclEntry.Builder

class DialogHelper(act: MainActivity) {
    private val activity = act
    private val accHelper = AccountHelper(activity)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = SignDialogBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        if (index == DialogConst.SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.ad_sign_up)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.ad_sign_in)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_in_action)
        }

        val dialog = builder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            dialog.dismiss()
            if (index == DialogConst.SIGN_UP_STATE) {
                accHelper.signUpWithEmail(
                    rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString()
                )
            } else {
                accHelper.signInWithEmail(
                    rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString()
                )
            }
        }
        dialog.show()
    }
}