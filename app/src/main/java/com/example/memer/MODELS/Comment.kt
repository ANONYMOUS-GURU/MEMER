package com.example.memer.MODELS

import com.google.firebase.firestore.DocumentSnapshot
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
    @ServerTimestamp
    val time : Date? = null
//    val commentLikeCount:Long
){
    companion object{
        fun DocumentSnapshot.toComment():Comment?{
            return Comment(
                getString("commentId") !!,
                getString("commentContent") !!,
                getString("commentParentId"),
                getString("commentOwnerId") !! ,
                getString("commentOwnerUsername") !! ,
                getString("commentOwnerUserAvatar") ,
                getString("commentPostOwnerId") !!,
                getString("commentPostId") !!,
                getLong("commentReplyCount") !!
            )
        }
    }
}
sealed class CommentState{
    object Default:CommentState()
    data class Edit(val position:Int,val parentIndex:Int):CommentState()
    data class ReplyTo(val userId:String,val username:String,val commentParentId: String,val parentIndex:Int):CommentState()
}