package com.example.memer.MODELS


data class PostContents2(
    val mPost: String,                                     // imageResourceReference,videoResourceReference from cloud storage
    var postId: String,                               // uniqueId for each post
    var postOwnerId:String,                           // unique id of the user who posted
    var postDescription:String?,                       // post description given by post owner
    val postTypeImage:Boolean,
    val postThumbnailReference: String,

    var likesList: ArrayList<String>?,
    var commentList: ArrayList<String>?,

)
