package com.QuickHelpVendor.Utils

import android.app.ProgressDialog
import android.content.Context


object  MyProgressDialog {
    private var progressBar: ProgressDialog? = null

    fun showProgressDialog(context: Context) {
        try {
            progressBar = ProgressDialog(context)
            progressBar!!.setMessage("Please wait...")
            progressBar!!.setCancelable(false)
            progressBar!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideProgressDialog() {
        try {
            if (progressBar!!.isShowing) {
                progressBar!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}