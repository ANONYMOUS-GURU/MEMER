package com.example.memer.MODELS

data class PostThumbnail(
    var thumbnailReference:String,
    var postId:String,
    var postOwnerId:String,
    var postTypeImage:Boolean = true
)
