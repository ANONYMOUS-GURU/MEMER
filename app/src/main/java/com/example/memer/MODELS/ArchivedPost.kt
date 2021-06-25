package com.example.memer.MODELS

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ArchivedPost(
    val postId:String,
    val userId:String,
    @ServerTimestamp
    val createdAt : Date? = null
)
