package com.example.memer.MODELS

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

// TODO(Whenever a ""NEW"" Like is written trigger a Cloud Function to write username,nameOfUser
//  and userAvatarReference to that document ...  So no need to send additional user info)

data class Likes(
    val likeId:String,
    val userId:String,
    val username:String,
    val nameOfUser:String,
    val userAvatarReference:String?,
    val likeType:String,
    val likeTypeId:String,
    @ServerTimestamp
    val createdAt : Date? = null
)

enum class LikeType(val value:String){
    CommentLike(value = "Comment"),
    PostLike(value = "Post")
}

data class LikedBy(
    val username:String,
    val userAvatarReference: String?,
    val nameOfUser:String,
    val userId:String,
){
    companion object{
        fun DocumentSnapshot.toLikedBy():LikedBy?{
            return LikedBy(
                username = getString("username")!!,
                userAvatarReference = getString("userAvatarReference"),
                nameOfUser = getString("nameOfUser")!!,
                userId = getString("userId")!!
            )
        }
    }
}