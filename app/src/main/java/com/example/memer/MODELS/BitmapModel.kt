package com.example.memer.MODELS

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BitmapModel(
    val id:Int,
    val bitmap: Bitmap,
    var leftMargin:Int = 0,
    var topMargin:Int = 0,
    var rightMargin:Int = 0,
    var bottomMargin:Int = 0,
    var angle:Float = 0f,
    var scale:Float = 1f
):Parcelable{
    companion object{
        fun Bitmap.toBitmapModel(id:Int):BitmapModel{
            return BitmapModel(id,this)
        }
    }
}
