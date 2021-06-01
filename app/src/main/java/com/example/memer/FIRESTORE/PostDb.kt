package com.example.memer.FIRESTORE

import android.util.Log
import com.example.memer.HELPERS.GLOBAL_INFORMATION.COMMENTS_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.COMMENT_REPLY_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.LIKES_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.LIKE_COUNT
import com.example.memer.HELPERS.GLOBAL_INFORMATION.POST_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_POST_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_RELATION_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.commentCount
import com.example.memer.HELPERS.GLOBAL_INFORMATION.commentReplyCount
import com.example.memer.HELPERS.GLOBAL_INFORMATION.createdAt
import com.example.memer.MODELS.Comment
import com.example.memer.MODELS.PostContents2
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


object PostDb {

    private val CACHE = Source.CACHE
    private val SERVER = Source.SERVER

    fun removePost(postId: String) {
        val db = FirebaseFirestore.getInstance()
    }

    fun removeComment(postId: String, commentId: String) {

    }

    fun addPost(
        imageReference: String,
        userId: String,
        postDesc: String,
        postId: String
    ): Task<Void> {

        val updates = HashMap<String, Any>()
        updates["timestamp"] = FieldValue.serverTimestamp()

        val post = PostContents2(
            postResource = imageReference,
            postId = postId,
            postOwnerId = userId,
            postDescription = postDesc,
            postTypeImage = true,
            likeCount = 0,
            commentCount = 0
        )

        val db = FirebaseFirestore.getInstance()
        val batch = FirebaseFirestore.getInstance().batch()
        batch.set(db.collection(POST_COLLECTION).document(postId), post)
        batch.set(
            db.collection(USER_COLLECTION).document(userId)
                .collection(USER_POST_COLLECTION).document(post.postId), post
        )
        batch.set(
            db.collection(USER_COLLECTION).document(userId),
            hashMapOf("userPostCount" to FieldValue.increment(1)), SetOptions.merge()
        )

        return batch.commit()
    }

    suspend fun getPosts(
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(POST_COLLECTION).orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(POST_COLLECTION).orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents
        Log.d(TAG, "getPosts: ${mDocuments.size}")


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }
        return mData
    }

    suspend fun getPostsUsers(
        lastDocument: DocumentSnapshot?,
        docLimit: Long,
        userId: String
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(USER_COLLECTION).document(userId).collection(USER_POST_COLLECTION)
                .orderBy(
                    "time", Query.Direction.DESCENDING
                )
                .limit(docLimit).get().await().documents
        else
            db.collection(USER_COLLECTION).document(userId).collection(USER_POST_COLLECTION)
                .orderBy(
                    "time", Query.Direction.DESCENDING
                )
                .startAfter(lastDocument).limit(docLimit).get().await().documents

        Log.d(TAG, "init block: Got Posts ${mDocuments.size}")

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }
        return mData
    }

    fun updateLikesPost(
        postId: String,
        userId: String,
        postOwnerId: String,
        incrementLike: Boolean
    ) {
        val db = FirebaseFirestore.getInstance()

        val batch = FirebaseFirestore.getInstance().batch()
        if (incrementLike) {
            Log.d(TAG, "updateLikesPost: Like ++ ")
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                    .document(userId),
                hashMapOf(LIKE_COUNT to 1, createdAt to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(LIKE_COUNT to FieldValue.increment(1)), SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                    .document(postOwnerId).collection(
                        LIKES_COLLECTION
                    ).document(postId),
                hashMapOf(LIKE_COUNT to 1, createdAt to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(
                    USER_POST_COLLECTION
                ).document(postId),
                hashMapOf(LIKE_COUNT to FieldValue.increment(1)),
                SetOptions.merge()
            )
        } else {
            batch.delete(
                db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                    .document(userId)
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(LIKE_COUNT to FieldValue.increment(-1)), SetOptions.merge()
            )
            batch.delete(
                db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                    .document(postOwnerId).collection(
                        LIKES_COLLECTION
                    ).document(postId)
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(
                    USER_POST_COLLECTION
                ).document(postId),
                hashMapOf(LIKE_COUNT to FieldValue.increment(-1)),
                SetOptions.merge()
            )
        }
        batch.commit()
    }

    suspend fun getUserLikes(
        postId: String,
        userId: String,
        postOwnerId: String
    ): Long {
        val db = FirebaseFirestore.getInstance()
        val userLike =
            db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                .document(postOwnerId).collection(LIKES_COLLECTION).document(postId).get().await()
        return userLike.getLong(LIKE_COUNT) ?: 0
    }

    fun addCommentPost(
        postId: String,
        userId: String,
        username:String,
        userAvatarReference:String?,
        postOwnerId: String,
        commentContent: String,
        commentParentId: String? = null
    ) {
        val db = FirebaseFirestore.getInstance()

        val commentId = userId + System.currentTimeMillis() + "Comment"
        val comment = Comment(
            commentId,
            commentContent,
            commentParentId,
            userId,
            username,
            userAvatarReference,
            postOwnerId,
            0
        )

        val batch = db.batch()

        batch.set(
            db.collection(POST_COLLECTION).document(postId),
            hashMapOf(commentCount to FieldValue.increment(1)),
            SetOptions.merge()
        )
        batch.set(
            db.collection(USER_COLLECTION).document(postOwnerId).collection(
                USER_POST_COLLECTION
            ).document(postId), hashMapOf(commentCount to FieldValue.increment(1))
        )
        batch.set(
            db.collection(USER_COLLECTION).document(userId).collection(
                USER_RELATION_COLLECTION
            ).document(postOwnerId).collection(COMMENTS_COLLECTION).document(postId),
            hashMapOf(commentId to comment), SetOptions.merge()
        )

        if (commentParentId == null) {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentId), comment,
                SetOptions.merge()
            )
        } else {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId)
                    .collection(COMMENT_REPLY_COLLECTION).document(commentId),
                comment,
                SetOptions.merge()
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId),
                hashMapOf(commentReplyCount to FieldValue.increment(1)), SetOptions.merge()
            )
        }

        batch.commit()
    }

    fun editComment(
        postId: String,
        userId: String,
        postOwnerId: String,
        commentContent: String,
        commentParentId: String? = null,
        commentId:String
    ) {

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        batch.set(
            db.collection(USER_COLLECTION).document(userId).collection(
                USER_RELATION_COLLECTION
            ).document(postOwnerId).collection(COMMENTS_COLLECTION).document(postId),
            hashMapOf("$commentId.commentContent" to commentContent), SetOptions.merge()
        )

        if (commentParentId == null) {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentId), hashMapOf("commentContent" to commentContent),
                SetOptions.merge()
            )
        } else {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId)
                    .collection(COMMENT_REPLY_COLLECTION).document(commentId),
                hashMapOf("commentContent" to commentContent),
                SetOptions.merge()
            )
        }

        batch.commit()
    }

    suspend fun getCommentPost(
        postId: String,
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    suspend fun getRepliesCommentsPost(
        postId: String,
        commentParentId: String,
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                .document(commentParentId).collection(COMMENT_REPLY_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                .document(commentParentId).collection(COMMENT_REPLY_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }


    // gets all comments on another user
    suspend fun getUsersCommentPost(
        postId: String,
        userId: String,
        postOwnerId: String
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        val mDocuments =
            db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                .document(postOwnerId).collection(
                    COMMENTS_COLLECTION
                ).orderBy("time", Query.Direction.DESCENDING)
                .get().await().documents

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    fun deleteComment() {

    }

    private const val TAG = "PostDb"

}