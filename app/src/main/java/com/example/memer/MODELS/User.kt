package com.example.memer.MODELS

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
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
    val userBookMarks: ArrayList<String> = ArrayList(),
    val userReports: ArrayList<String> = ArrayList(),
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
                get("userBookMarks") as ArrayList<String>,
                get("userReports") as ArrayList<String>,
                getString("userAvatarReference"),
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
                userBookMarks = ArrayList(),
                userReports = ArrayList(),
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

data class UserNonEditInfo(
    var followersCount: Long = 0,
    var followingCount: Long = 0,
    var postCount: Long = 0
)

data class UserEditableInfo(
    var nameOfUser:String = "Name of User",
    var username:String = "$$$",   // A placeholder for no username
    var bio:String = ""
)

data class UserProfileInfo(
    var nameOfUser:String,
    var username:String,
    var postCount:Int,
    var userId:String?,
    var followersCount:Long,
    var followingCount:Long,
    var bio:String,
    var imageUserReference:String
)

data class UserDisplayInfo(
    val username : String,
    val userAvatarReference:String?,
    val userId : String
)
