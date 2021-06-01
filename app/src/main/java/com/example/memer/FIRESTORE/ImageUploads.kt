package com.example.memer.FIRESTORE

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


object ImageUploads {

    private const val POST_IMAGE_PATH = "POST_IMAGES"
    private const val USER_PROFILE_IMAGE_PATH = "USER_PROFILE"
    private val storageRef = Firebase.storage.reference

    fun uploadBitmapPost(bitmap: Bitmap,userId:String,uniqueId:String) : UploadTask{
        // time no special Char [Check]
        val imageRef = storageRef.child(userId).child(POST_IMAGE_PATH).child(uniqueId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        return imageRef.putBytes(data)
    }
    fun uploadBitmapProfile(bitmap: Bitmap,userId:String,uniqueId:String) : UploadTask{
        val imageRef = storageRef.child(userId).child(USER_PROFILE_IMAGE_PATH).child(uniqueId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        return imageRef.putBytes(data)
    }
    fun getDownloadUrlPost(userId: String,uniqueId:String) : Task<Uri>{
        return storageRef.child(userId).child(POST_IMAGE_PATH).child(uniqueId).downloadUrl
    }
    fun getDownloadUrlProfile(userId: String,uniqueId:String) : Task<Uri>{
        return storageRef.child(userId).child(USER_PROFILE_IMAGE_PATH).child(uniqueId).downloadUrl
    }

}