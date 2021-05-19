package com.example.memer.MODELS

data class CommentChild(
    var commentContent:String,
    val commentOwnerId:String,
    val commentLikeList:ArrayList<User>,
    val commentParentId:String
)
