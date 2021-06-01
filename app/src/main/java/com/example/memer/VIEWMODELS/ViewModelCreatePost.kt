package com.example.memer.VIEWMODELS

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelCreatePost : ViewModel() {

    var uriData : Uri? = null
    var bitmapData : Bitmap? = null

    private var allBitmap  =  ArrayList<Bitmap>()
    private val allBitmapMLD : MutableLiveData<List<Bitmap>> = MutableLiveData<List<Bitmap>>()
    val allBitmapLD : LiveData<List<Bitmap>>
        get() = allBitmapMLD

    var finalBitmap:Bitmap ? = null


    fun update(bitmap: Bitmap){
        allBitmap.add(bitmap)
        allBitmapMLD.value = allBitmap
    }



}