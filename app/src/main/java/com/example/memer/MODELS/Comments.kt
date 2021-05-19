package com.example.memer.MODELS

data class Comments(
    val commentId: String,
    var commentContent:String,
    val commentOwnerId:String,
    val commentLikeList:String,
    val commentReplyList:ArrayList<String>
)
