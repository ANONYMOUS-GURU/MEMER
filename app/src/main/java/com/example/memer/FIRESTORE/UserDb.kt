package com.example.memer.FIRESTORE

import com.example.memer.HELPERS.MyUser
import com.example.memer.MODELS.UserEditableInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

object UserDb{

    private const val USER_COLLECTION = "USERS"
    val userId = MyUser.getUser()!!.userId

    fun updateImageReference(imageReference:Pair<String?,String?>){
        val data = mapOf(
            "userProfilePicReference" to imageReference.first,
            "userAvatarReference" to imageReference.second
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userId).set(data, SetOptions.merge())
    }
    fun updateEditableInfo(userEditableInfo: UserEditableInfo){
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userId).set(userEditableInfo, SetOptions.merge())
    }

    fun updateUserFollowerList(idOfUser:String,type:String = "ADD"){
        val db = FirebaseFirestore.getInstance()
    }
    fun updateUserFollowingList(idOfUser:String , type:String = "ADD"){
        val db = FirebaseFirestore.getInstance()
    }
    fun removePost(postId:String){
        val db = FirebaseFirestore.getInstance()
    }

    fun addPost(){

    }

    fun updateLike( id :String , type: String = "POST"){

    }

    fun updateComment(){

    }

    fun updateBookMark(postId: String,type: String = "ADD"){

    }

    fun updateReport(postId: String,type: String = "ADD"){

    }



}