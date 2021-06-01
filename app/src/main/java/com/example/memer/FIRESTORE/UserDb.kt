package com.example.memer.FIRESTORE

import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_RELATION_COLLECTION
import com.example.memer.MODELS.*
import com.example.memer.MODELS.UserData.Companion.toUserData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object UserDb{

    private const val TAG = "UserDb"


    fun updateImageReference(imageReference:Pair<String?,String?>,userId: String){
        val data = mapOf(
            "userProfilePicReference" to imageReference.first,
            "userAvatarReference" to imageReference.second
        )
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userId).set(data, SetOptions.merge())
    }
    fun updateUser(user:UserData){
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(user.userId).set(user, SetOptions.merge())
    }

    suspend fun getUserDisplayInfo(userId:String): UserDisplayInfo {
        val db = FirebaseFirestore.getInstance()
        val mUser = db.collection(USER_COLLECTION).document(userId).get().await()

        return UserDisplayInfo(mUser.getString("username") !!,
        mUser.getString("userAvatarReference") ,mUser.getString("userId") !!)

    }
    suspend fun addNewUser(userData: UserData){
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userData.userId).set(userData).await()
    }
    suspend fun getOldUser(mUser: FirebaseUser) : UserData? {
        val db = FirebaseFirestore.getInstance()
        return db.collection(USER_COLLECTION).document(mUser.uid).get().await().toUserData()
    }


    fun updateEditableInfo(userEditableInfo: UserEditableInfo,userId: String){
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userId).set(userEditableInfo, SetOptions.merge())
    }
    fun updatePostCount(userNonEditInfo: UserNonEditInfo,userId: String){
        val db = FirebaseFirestore.getInstance()
        db.collection(USER_COLLECTION).document(userId).set(userNonEditInfo, SetOptions.merge())
    }
    fun updateUserFollowerList(idOfUser:String,type:String = "ADD"){
        val db = FirebaseFirestore.getInstance()
    }
    fun updateUserFollowingList(idOfUser:String , type:String = "ADD"){
        val db = FirebaseFirestore.getInstance()
    }
    fun updateBookMark(postId: String,type: String = "ADD"){
    }
    fun updateReport(postId: String,type: String = "ADD"){
    }

}