package com.example.memer.MODELS

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Comment(
    val commentId:String,
    val commentContent:String,
    val commentParentId:String?,
    val commentOwnerId:String,
    val commentOwnerUsername:String,
    val commentOwnerUserAvatar:String?,
    val commentPostId:String,
    val commentReplyCount:Long = 0,
    @ServerTimestamp
    val time : Date? = null
//    val commentLikeCount:Long
){
    companion object{
        fun DocumentSnapshot.toComment():Comment?{
            return Comment(
                getString("commentId") !!,
                getString("commentContent") !!,
                getString("commentParentId"),
                getString("commentOwnerId") !! ,
                getString("commentOwnerUsername") !! ,
                getString("commentOwnerUserAvatar") ,
                getString("commentPostId") !!,
                getLong("commentReplyCount") !!
            )
        }
    }
}
