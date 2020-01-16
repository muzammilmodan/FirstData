package com.QuickHelpVendor.Utils

import android.R
import android.content.Context
import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar


object  SnackBar {
    fun showInternetError(context: Context, view: View) {
        val snackbar = Snackbar
            .make(view, "Please check your internet connection.", Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setDuration(10000)
        val snackbarView = snackbar.getView()
        //        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(Color.BLACK)
        snackbar.show()
    }

    fun showValidationError(context: Context, view: View, msg: String) {
        val snackbar = Snackbar
            .make(view, msg, Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.getView()
        snackbarView.setBackgroundColor(Color.BLACK)
        //        snackbarView.setBackgroundColor(Color.RED);
        snackbar.show()
    }


    fun showError(context: Context, view: View, msg: String) {
        val snackbar = Snackbar
            .make(view, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.getView()
        //        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(Color.RED)
        snackbar.show()
    }

    fun showSuccess(context: Context, view: View, msg: String) {
        val snackbar = Snackbar
            .make(view, msg, Snackbar.LENGTH_SHORT)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.getView()
        //        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbarView.setBackgroundColor(Color.GREEN)
        snackbar.show()
    }

    fun showInProgressError(context: Context, view: View) {
        val msg = "Working In Progress."

        val snackbar = Snackbar
            .make(view, msg, Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.getView()
        snackbarView.setBackgroundColor(Color.GRAY)
        //        snackbarView.setBackgroundColor(Color.RED);
        snackbar.show()
    }
}