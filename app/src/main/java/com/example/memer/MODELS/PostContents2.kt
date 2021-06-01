package com.example.memer.MODELS

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PostContents2(
    val postResource: String,                                     // imageResourceReference,videoResourceReference from cloud storage
    val postId: String,                               // uniqueId for each post
    val postOwnerId:String,                           // unique id of the user who posted
    val postDescription:String,                       // post description given by post owner
    val postTypeImage:Boolean,
    val likeCount: Long,
    val commentCount: Long,
    @ServerTimestamp
    val time :Date? = null,

):Parcelable{
    companion object{
        fun DocumentSnapshot.toPostContents2() : PostContents2{
            return PostContents2(
                getString("postResource") !!,
                getString("postId") !!,
                getString("postOwnerId") !!,
                getString("postDescription") !!,
                getBoolean("postTypeImage") !!,
                getLong("likeCount") !!,
                getLong("commentCount") !!
            )
        }
    }
}



data class PostHomePage(
    val postContents: PostContents2,
    var likeCount : Long = 0,
    var isBookmarked: Boolean =  false,
    var isCommented : Boolean = false,
    var username:String? = null,
    var userAvatarReference:String? = null
)
