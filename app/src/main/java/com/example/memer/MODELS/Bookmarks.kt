package com.example.memer.MODELS

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

//TODO(Send only postId and userId to CLoud Function)
data class Bookmarks(
    val bookmarkId:String,
    val postId:String,
    val userId:String,

    val postOwnerId:String,
    val postOwnerUsername:String,
    val postOwnerAvatarReference:String?,

    @ServerTimestamp
    val createdAt : Date? = null
)
