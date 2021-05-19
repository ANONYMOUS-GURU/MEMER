package com.example.memer.MODELS

data class User(
    val userId:String,
    val username:String,
    val nameOfUser:String,
    val signInType:String,
    val phoneNumber:String?,
    val postsReference:ArrayList<PostContents>?,
    val userAvatarReference:String?,
    val userProfilePicReference:String?,
    val userComments:ArrayList<String>?, // -> String = commentId
    val userLikes:ArrayList<String>?,   // -> string = postId
    val userFollowers:ArrayList<String>?,
    val userFollowing:ArrayList<String>?,
    val userBookMarks:ArrayList<String>?,
    val userReports:ArrayList<String>?
)
