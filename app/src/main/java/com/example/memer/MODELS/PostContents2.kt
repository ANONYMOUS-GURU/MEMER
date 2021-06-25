package com.example.memer.MODELS

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PostContents2(
    val postId: String,                               // uniqueId for each post

    val postOwnerId:String,                           // unique id of the user who posted

    val postResource: String,                                     // imageResourceReference,videoResourceReference from cloud storage
    var postDescription:String,                       // post description given by post owner
    val postTypeImage:Boolean,

    val username:String,
    var userAvatarReference:String? = null,

    val likeCount: Long,
    val commentCount: Long,
    val bookMarkCount:Long? = null,
    val reportCount:Long? = null,

    @ServerTimestamp
    val createdAt :Date? = null,
    @ServerTimestamp
    val updatedAt:Date? = null

):Parcelable{
    companion object{
        fun DocumentSnapshot.toPostContents2() : PostContents2{
            return PostContents2(
                postId = getString("postId") !!,
                postOwnerId = getString("postOwnerId") !!,

                postResource = getString("postResource") !!,
                postDescription = getString("postDescription") !!,
                postTypeImage = getBoolean("postTypeImage") !!,

                username = getString("username") !!,
                userAvatarReference = getString("userAvatarReference"),

                likeCount = getLong("likeCount") !!,
                commentCount = getLong("commentCount") !!,
                bookMarkCount = getLong("bookMarkCount"),

                reportCount = getLong("reportCount")
            )
        }
    }
}

@Parcelize
data class PostHomePage(
    val postContents: PostContents2,
    var isLiked : Boolean = false,
    var isBookmarked: Boolean =  false,
    var isCommented : Boolean = false,
):Parcelable
