package com.example.memer.MODELS

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.auth.User
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Array.getInt
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign

@Parcelize
data class UserData(
    val userId: String,
    val username: String,
    val nameOfUser: String,

    val signInType: String,
    val phoneNumber: String?,
    val bio: String,
    var userProfilePicReference: String? = null,

    val userPostCount : Long,
    var userAvatarReference: String? = null,
    val userGlobalLikes:Long,

    @ServerTimestamp
    val createdAt : Date? = null,
    @ServerTimestamp
    val updatedAt:Date? = null
):Parcelable {
    companion object {
        fun DocumentSnapshot.toUserData(): UserData? {
            return UserData(
                userId = getString("userId")!!,
                username = getString("username")!!,
                nameOfUser = getString("nameOfUser")!!,

                signInType = getString("signInType")!!,
                phoneNumber = getString("phoneNumber"),
                bio = getString("bio")!!,
                userProfilePicReference = getString("userProfilePicReference"),

                userPostCount = getLong("userPostCount") !!,
                userAvatarReference = getString("userAvatarReference"),
                userGlobalLikes = getLong("userGlobalLikes")!!
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

data class UserProfileInfo(
    var nameOfUser:String,
    var username:String,
    var postCount:Long,
    var userId:String,
    var bio:String,
    var userAvatarReference:String?,
    val userGlobalLikes:Long,
){
    companion object {
        fun UserData.toUserProfileInfo():UserProfileInfo{
            return UserProfileInfo(
                nameOfUser = nameOfUser,
                username = username,
                postCount = userPostCount,
                userId = userId ,
                bio = bio,
                userAvatarReference = userAvatarReference,
                userGlobalLikes = userGlobalLikes
            )
        }

        fun DocumentSnapshot.toUserProfileInfo():UserProfileInfo{
            return UserProfileInfo(
                nameOfUser = getString("nameOfUser")!!,
                username = getString("username")!!,
                postCount = getLong("userPostCount")!!,
                userId = getString("userId")!!,
                bio = getString("bio")!!,
                userAvatarReference = getString("userAvatarReference"),
                userGlobalLikes = getLong("userGlobalLikes")!!
            )
        }
    }
}

data class UserTextEditInfo(
    val username: String,
    val nameOfUser: String,
    val bio:String,
    var userAvatarReference: String?
)

data class UserEditInfo(
    val username: String,
    val nameOfUser: String,
    val bio:String,
    val userAvatarReference: String?,
    val userProfilePicReference: String?
){
    companion object{
        fun UserData.toUserEditInfo():UserEditInfo{
            return UserEditInfo(username = username,
                nameOfUser = nameOfUser,
                bio = bio,
                userAvatarReference = userAvatarReference,
                userProfilePicReference = userProfilePicReference
            )
        }
    }
}
