package com.example.firstkotlinapp.util

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.example.firstkotlinapp.R


class ProgressDialog : Application() {
    private lateinit var dialog: AppCompatDialog

    companion object {
        private var progressDialog: ProgressDialog? = null

        fun getInstance(): ProgressDialog {
            return progressDialog ?: synchronized(this) {
                progressDialog ?: ProgressDialog().also { progressDialog = it }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        progressDialog = this
    }

    fun progressON(activity: Activity) {
        dialog = AppCompatDialog(activity)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.loading_progress_dialog)
        dialog.show()

        val imageView = dialog.findViewById<ImageView>(R.id.loading_prpgress_imageview)
        Glide.with(activity).load(R.drawable.ic_loading2).into(imageView!!)
    }

    fun progressOFF() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }


}