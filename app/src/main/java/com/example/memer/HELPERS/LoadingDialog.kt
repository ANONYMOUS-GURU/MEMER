package com.example.memer.HELPERS

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.memer.R
import kotlinx.android.synthetic.main.loading_dialog.*

class LoadingDialog(private val activity: Activity) {

    private lateinit var dialog:AlertDialog

    fun startLoadingDialog(text:String = "Loading..."){
        val builder:AlertDialog.Builder = AlertDialog.Builder(activity)

        val inflater:LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog,null))

        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

        dialog.dialogText.text=text
    }

    fun dismissDialog(){
        dialog.dismiss()
    }

     fun changeText(text:String = "Loading..."){
        dialog.dialogText.text=text
    }
}