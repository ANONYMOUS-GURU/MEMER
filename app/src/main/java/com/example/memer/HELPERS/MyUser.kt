package com.example.memer.HELPERS

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.memer.HELPERS.MyUser.UserData.Companion.toUserData
import com.example.memer.MODELS.PostContents
import com.example.memer.MODELS.UserEditableInfo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await

object MyUser {

    private const val USERS = "USERS"
    private const val TAG = "MyUser"

    data class UserData(
        val userId: String,
        val username: String,
        val nameOfUser: String,
        val signInType: String,
        val phoneNumber: String?,
        var bio: String = "",
        var userProfilePicReference: String? = null,
        var userComments: ArrayList<String> = ArrayList(),
        var userLikes: ArrayList<String> = ArrayList(),
        var userFollowers: ArrayList<String> = ArrayList(),
        var userFollowing: ArrayList<String> = ArrayList(),
        var userBookMarks: ArrayList<String> = ArrayList(),
        var userReports: ArrayList<String> = ArrayList(),
        var postsReference: ArrayList<PostContents> = ArrayList(),
        var userAvatarReference: String? = null,
        @Exclude @set:Exclude @get:Exclude var isAuthenticated: Boolean = false,
        @Exclude @set:Exclude @get:Exclude var isNewUser: Boolean = true
    ) {
        companion object {
            fun DocumentSnapshot.toUserData(): UserData? {
                return UserData(
                    getString("userId")!!,
                    getString("username")!!,
                    getString("nameOfUser")!!,
                    getString("signInType")!!,
                    getString("phoneNumber"),
                    getString("bio")!!,
                    getString("userProfilePicReference"),
                    get("userComments") as ArrayList<String>,
                    get("userLikes") as ArrayList<String>,
                    get("userFollowers") as ArrayList<String>,
                    get("userFollowing") as ArrayList<String>,
                    get("userBookMarks") as ArrayList<String>,
                    get("userReports") as ArrayList<String>,
                    get("postsReference") as ArrayList<PostContents>,
                    getString("userAvatarReference")
                )
            }

            fun UserBasicInfo.toUserData(): UserData {
                return UserData(
                    userId = userId,
                    username = username,
                    nameOfUser = nameOfUser,
                    signInType = signInType,
                    phoneNumber = phoneNumber,
                    bio = "",
                    userProfilePicReference = null,
                    userComments = ArrayList(),
                    userLikes = ArrayList(),
                    userFollowers = ArrayList(),
                    userFollowing = ArrayList(),
                    userBookMarks = ArrayList(),
                    userReports = ArrayList(),
                    postsReference = ArrayList(),
                    userAvatarReference = null,
                    isAuthenticated = false,
                    isNewUser = true,
                )
            }
        }
    }

    data class UserBasicInfo(
        val userId: String,
        val username: String,
        val nameOfUser: String,
        val signInType: String,
        val phoneNumber: String?
    )

    private var user: UserData? = null


    suspend fun updateNewUser(userBasicInfo: UserBasicInfo) {
        val db = FirebaseFirestore.getInstance()
        db.collection(USERS).document(userBasicInfo.userId)
            .set(userBasicInfo.toUserData(), SetOptions.merge()).await()
        user = db.collection(USERS).document(userBasicInfo.userId).get().await().toUserData()
        user!!.isAuthenticated = true
        user!!.isNewUser = true
    }

    suspend fun updateUserEditInfo(_user: UserEditableInfo) {
        val db = FirebaseFirestore.getInstance()
        db.collection(USERS).document(user!!.userId).set(_user, SetOptions.merge()).await()
        user = db.collection(USERS).document(user!!.userId).get().await().toUserData()
        user!!.isNewUser = false
    }

    suspend fun authenticateUser(mUser: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val st = System.currentTimeMillis()
        user = db.collection(USERS).document(mUser.uid).get().await().toUserData()
        user!!.isNewUser = false
        user!!.isAuthenticated = true
    }

    suspend fun initializeUser() {
        Log.d(TAG, "initializeUser: Started")
        Log.d(TAG, "initializeUser: delay ended")
        val mUser = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()
        if (mUser != null) {
            Log.d(TAG, "initializeUser: user not null initializing")
            user = db.collection(USERS).document(mUser.uid).get().await().toUserData()
            user!!.isAuthenticated = true
            user!!.isNewUser = false
            Log.d(TAG, "initializeUser: initialized")
        } else {
            Log.d(TAG, "initializeUser: user is null")
            user = null
        }
    }

    fun getUser(): UserData? {
        return user
    }

}

