package com.example.memer.MODELS

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Comment(
    val commentId:String,
    var commentContent:String,
    val commentParentId:String?,

    val commentOwnerId:String,
    val commentOwnerUsername:String,
    val commentOwnerUserAvatar:String?,

    val commentPostOwnerId:String,
    val commentPostId:String,

    var commentReplyCount:Long = 0,
    var commentLikeCount:Long = 0,

    @ServerTimestamp
    val createdAt : Date? = null,
    @ServerTimestamp
    val updatedAt:Date? = null,

    @Exclude @set:Exclude @get:Exclude var isLiked: Boolean = false
){
    companion object{
        fun DocumentSnapshot.toComment():Comment?{
            return Comment(
                commentId = getString("commentId") !!,
                commentContent = getString("commentContent") !!,
                commentParentId = getString("commentParentId"),
                commentOwnerId = getString("commentOwnerId") !! ,
                commentOwnerUsername = getString("commentOwnerUsername") !! ,
                commentOwnerUserAvatar = getString("commentOwnerUserAvatar") ,
                commentPostOwnerId = getString("commentPostOwnerId") !!,
                commentPostId = getString("commentPostId") !!,
                commentReplyCount = getLong("commentReplyCount") !!,
                commentLikeCount = getLong("commentLikeCount")!!
            )
        }
    }
}
sealed class CommentState{
    object Default:CommentState()
    data class Edit(val position:Int,val parentIndex:Int):CommentState()
    data class ReplyTo(val userId:String,val username:String,val commentParentId: String,val parentIndex:Int):CommentState()
}