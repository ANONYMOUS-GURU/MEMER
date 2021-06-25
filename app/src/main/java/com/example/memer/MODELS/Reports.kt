package com.example.memer.MODELS

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

//TODO(Send only postId/commentId , ReportType and userId to CLoud Function)
sealed class Reports{
    data class PostReport(
        val reportId:String,
        val reportType:String = "POST",
        val postId:String,
        val postOwnerId:String,
        val postOwnerUsername:String,
        val postOwnerAvatarReference:String?,
        val userId:String,
        @ServerTimestamp
        val createdAt : Date? = null
    ):Reports()
    data class CommentReport(
        val reportId:String,
        val reportType: String = "COMMENT",
        val commentId:String,
        val commentOwnerId:String,
        val commentOwnerUsername:String,
        val commentOwnerAvatarReference:String?,
        val userId:String,
        @ServerTimestamp
        val createdAt : Date? = null
    ):Reports()
    data class UserReport(
        val reportId:String,
        val reportType:String = "USER",
        val reportedUserId:String,
        val reportedUserAvatarReference:String?,
        val reportedUserUsername:String,
        val userId:String,
        @ServerTimestamp
        val createdAt : Date? = null
    ):Reports()
}