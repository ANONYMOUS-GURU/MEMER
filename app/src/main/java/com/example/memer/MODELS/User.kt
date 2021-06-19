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
    val userFollowersCount:Long,
    val userFollowingCount:Long,
    var userAvatarReference: String? = null,
    @Exclude @set:Exclude @get:Exclude var isAuthenticated: Boolean = false,
    @Exclude @set:Exclude @get:Exclude var isNewUser: Boolean = true,
    @ServerTimestamp
    val time : Date? = null
):Parcelable {
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
                get("userPostCount") as Long,
                get("userFollowersCount") as Long,
                get("userFollowingCount") as Long,
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
                userPostCount = 0,
                userFollowersCount = 0,
                userFollowingCount = 0,
                userAvatarReference = null
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
    var userId:String?,
    var followersCount:Long,
    var followingCount:Long,
    var bio:String,
    var userAvatarReference:String?
){
    companion object {
        fun UserData.toUserProfileInfo():UserProfileInfo{
            return UserProfileInfo(nameOfUser,username,userPostCount,userId,
                userFollowersCount,userFollowingCount,bio,userAvatarReference)
        }

        fun DocumentSnapshot.toUserProfileInfo():UserProfileInfo{
            return UserProfileInfo(
                getString("nameOfUser")!!,
                getString("username")!!,
                getLong("postCount")!!,
                getString("userId")!!,
                getLong("followersCount")!!,
                getLong("followingCount")!!,
                getString("bio")!!,
                getString("userAvatarReference"),
            )
        }
    }
}

data class LikedBy(
    val username:String,
    val userAvatarReference: String?,
    val nameOfUser:String,
    val userId:String,
    var isFollowing:Boolean = false
){
    companion object{
        fun DocumentSnapshot.toLikedBy():LikedBy?{
            return LikedBy(
                getString("username")!!,
                getString("userAvatarReference"),
                getString("nameOfUser")!!,
                getString("userId")!!
            )
        }
    }
}

