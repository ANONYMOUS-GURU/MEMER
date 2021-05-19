package com.example.memer.MODELS

// likesArray replaced by like counts                    and      a reference to entire list if asked
// comments replaced by comment top 2 and comment count  and      a reference to entire list if asked

data class PostContents(
    var mPost: String,                                     // imageResourceReference,videoResourceReference from cloud storage
    var username: String,                                  // username may or may not be unique
    var userAvatar: String? = null,                        // imageResourceReference for thumbnail cloud storage
    var postId: String = "",                               // uniqueId for each post

    var likesCount:Int = 0,
    var likesListReference: String = "",

    var comments: Pair<Comments,Comments>? = null,
    var commentListReference:String = "",

    var postOwnerId:String = "",                           // unique id of the user who posted
    var postTagList:ArrayList<String>? = null,             // peopleId tagged in the post
    var postDescription:String = "",                       // post description given by post owner

    var isFollowing:Boolean = false,
    var isPrivate:Boolean = true,
    var isFollower:Boolean = false,

    var isBookMarked:Boolean = false,
    var isLiked:Boolean = false

)




