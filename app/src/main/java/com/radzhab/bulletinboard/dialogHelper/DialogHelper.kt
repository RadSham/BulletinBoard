package com.radzhab.bulletinboard.dialogHelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.radzhab.bulletinboard.MainActivity
import com.radzhab.bulletinboard.R
import com.radzhab.bulletinboard.accountHelper.AccountHelper
import com.radzhab.bulletinboard.databinding.SignDialogBinding

class DialogHelper(val activity: MainActivity) {
    val accHelper = AccountHelper(activity)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = SignDialogBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        setDialogState(index, rootDialogElement)

        val dialog = builder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, rootDialogElement, dialog)
        }
        rootDialogElement.btForgetP.setOnClickListener {
            setOnClickPressedPassword(rootDialogElement, dialog)
        }
        rootDialogElement.btGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
            dialog?.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickPressedPassword(
        rootDialogElement: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        if (rootDialogElement.edSignEmail.text.isNotEmpty()) {
            activity.myAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activity,
                            R.string.email_reset_password_was_sent,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            dialog?.dismiss()

        } else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(
        index: Int,
        rootDialogElement: SignDialogBinding,
        dialog: AlertDialog?
    ) {
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
        dialog?.dismiss()

    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if (index == DialogConst.SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.ad_sign_up)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.ad_sign_in)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_in_action)
            rootDialogElement.btForgetP.visibility = View.VISIBLE
        }
    }

}