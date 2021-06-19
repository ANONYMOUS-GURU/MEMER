package com.example.memer.VIEWMODELS

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.MODELS.BitmapModel

class ViewModelCreatePost : ViewModel() {

    var uriData : Uri? = null
    var bitmapData : Bitmap? = null
    var photoMap = HashMap<Int,BitmapModel>()


    private val croppedBitmapMLD : MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val croppedBitmapLD : LiveData<Bitmap>
        get() = croppedBitmapMLD

    
    init {
        Log.d(TAG, "init: ")
    }
    
    var finalBitmap:Bitmap ? = null


    fun update(bitmap: Bitmap){
        Log.d(TAG, "update: updating Bitmap")
        croppedBitmapMLD.value = bitmap
    }

    companion object{
        private const val TAG = "VMCreatePost"
    }


}