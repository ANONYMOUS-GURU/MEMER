package com.example.memer.MODELS

import java.lang.ref.Reference

data class PostContents2(
    val mPost: String,                                     // imageResourceReference,videoResourceReference from cloud storage
    var postId: String,                               // uniqueId for each post
    var likesList: ArrayList<String>?,
    var commentList: ArrayList<String>?,
    var postOwnerId:String,                           // unique id of the user who posted
    var postTagList:ArrayList<String>?,             // peopleId tagged in the post
    var postDescription:String?,                       // post description given by post owner
    val postTypeImage:Boolean,
    val postThumbnailReference: String

)
