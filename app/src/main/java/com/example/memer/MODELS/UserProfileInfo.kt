package com.example.memer.MODELS

data class UserProfileInfo(
    var nameOfUser:String,
    var username:String,
    var postCount:Int,
    var userId:String?,
    var followersCount:Int,
    var followingCount:Int,
    var bio:String,
    var imageUserReference:String
)
