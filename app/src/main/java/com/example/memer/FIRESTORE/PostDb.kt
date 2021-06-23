package com.example.memer.FIRESTORE

import android.util.Log
import com.example.memer.HELPERS.GLOBAL_INFORMATION.BOOKMARK_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.COMMENTS_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.COMMENT_REPLY_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.LIKES_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.POST_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_POST_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_RELATION_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.bookMarkCountElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.commentCountElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.commentReplyCountElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.createdAtElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.likeCountElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.nameOfUserElement
import com.example.memer.HELPERS.GLOBAL_INFORMATION.userIdElement
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


    //TODO(EVERYTHING ON ADD POST INSIDE A CLOUD FUNCTION IN AWAIT FASHION AND FAIL IF NO INTERNET)
    fun addPost(
        imageReference: String,
        userId: String,
        username: String,
        userAvatarReference: String?,
        postDesc: String,
        postId: String,
        isPublic:Boolean = true
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
            commentCount = 0,
            username = username,
            userAvatarReference = userAvatarReference,
            public = isPublic,
            bookMarkCount = 0
        )

        val db = FirebaseFirestore.getInstance()
        val batch = FirebaseFirestore.getInstance().batch()
        batch.set(db.collection(POST_COLLECTION).document(postId), post)
        batch.set(
            db.collection(USER_COLLECTION).document(userId)
                .collection(USER_POST_COLLECTION).document(postId), post
        )
        batch.set(
            db.collection(USER_COLLECTION).document(userId),
            hashMapOf("userPostCount" to FieldValue.increment(1)), SetOptions.merge()
        )

        return batch.commit()
    }

    //TODO(GET POSTS IMPLEMENT USING CLOUD FUNCTION FOR GETTING USER LIKES BOOKMARKS AND GETTING IT IN ACCORDANCE WITH USER RELATIONS ETC.)
    suspend fun getPosts(lastDocument: DocumentSnapshot?, docLimit: Long,userId: String)
    : ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()


        val mDocuments = if (lastDocument == null)
            db.collection(POST_COLLECTION).orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(POST_COLLECTION).orderBy("time", Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(docLimit).get()
                .await().documents
        Log.d(TAG, "getPosts: ${mDocuments.size}")


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }
        Log.d(TAG, "getPosts: returning")
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
        postOwnerId: String,
        userId: String,
        username:String,
        userAvatarReference:String?,
        nameOfUser:String,
        incrementLike: Boolean
    ) {
        val db = FirebaseFirestore.getInstance()

        val batch = FirebaseFirestore.getInstance().batch()         // TODO(APART FROM USER_COLLECTION EVERYTHING ELSE SHOULD BE HANDLED BY CLOUD FUNCTION FOR SECURITY)
        if (incrementLike) {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                    .document(userId),
                hashMapOf("userId" to userId,"username" to username,"userAvatarReference" to userAvatarReference,
                    likeCountElement to 1, createdAtElement to FieldValue.serverTimestamp() , nameOfUserElement to nameOfUser),
                SetOptions.merge()
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(likeCountElement to FieldValue.increment(1)), SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)  // TODO(CLOUD FUNCTION TRIGGERED ON THIS WRITE)
                    .document(postOwnerId).collection(
                        LIKES_COLLECTION
                    ).document(postId),
                hashMapOf(likeCountElement to 1, createdAtElement to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(
                    USER_POST_COLLECTION
                ).document(postId),
                hashMapOf(likeCountElement to FieldValue.increment(1)),
                SetOptions.merge()
            )
        } else {
            batch.delete(
                db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                    .document(userId)
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(likeCountElement to FieldValue.increment(-1)), SetOptions.merge()
            )
            batch.delete(
                db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)   // TODO(CLOUD FUNCTION TRIGGERED ON THIS WRITE)
                    .document(postOwnerId).collection(
                        LIKES_COLLECTION
                    ).document(postId)
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(
                    USER_POST_COLLECTION
                ).document(postId),
                hashMapOf(likeCountElement to FieldValue.increment(-1)),
                SetOptions.merge()
            )
        }
        batch.commit()
    }

    // TODO(THIS FUNCTION WILL BE DELETED AS LIKES WILL BE RETRIEVED BY A CLOUD FUNCTION ALONG WITH GET POSTS)
    suspend fun getUserLikes(
        postId: String,
        userId: String,
        postOwnerId: String
    ): Long {
        val db = FirebaseFirestore.getInstance()
        val userLike =
            db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                .document(postOwnerId).collection(LIKES_COLLECTION).document(postId).get().await()
        return userLike.getLong(likeCountElement) ?: 0
    }

    suspend fun getUserBookMarks(
        postId: String,
        userId: String,
        postOwnerId: String
    ): Boolean {
        val db = FirebaseFirestore.getInstance()
        val userBookMark =
            db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                .document(postOwnerId).collection(BOOKMARK_COLLECTION).document(postId).get().await()
        val ret =  userBookMark.getLong(bookMarkCountElement) ?: 0
        return ret == 1L
    }

    fun addCommentPost(
        comment:Comment,
        postId: String,
        userId: String,
        postOwnerId: String,
        commentParentId: String? = null
    ) {
        val db = FirebaseFirestore.getInstance()

        val batch = db.batch()

        batch.set(
            db.collection(POST_COLLECTION).document(postId),
            hashMapOf(commentCountElement to FieldValue.increment(1)),
            SetOptions.merge()
        )
        batch.set(
            db.collection(USER_COLLECTION).document(postOwnerId).collection(
                USER_POST_COLLECTION
            ).document(postId), hashMapOf(commentCountElement to FieldValue.increment(1)), SetOptions.merge()
        )
        batch.set(
            db.collection(USER_COLLECTION).document(userId).collection(                  // TODO(THIS WRITE TRIGGERS EVERY OTHER WRITE IN DB USING CLOUD FUNCTIONS)
                USER_RELATION_COLLECTION
            ).document(postOwnerId).collection(COMMENTS_COLLECTION).document(postId),
            hashMapOf(comment.commentId to comment), SetOptions.merge()
        )

        if (commentParentId == null) {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(comment.commentId), comment,
                SetOptions.merge()
            )
        } else {
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId)
                    .collection(COMMENT_REPLY_COLLECTION).document(comment.commentId),
                comment,
                SetOptions.merge()
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId),
                hashMapOf(commentReplyCountElement to FieldValue.increment(1)), SetOptions.merge()
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

        batch.update(
            db.collection(USER_COLLECTION).document(userId).collection(                     // TODO(THIS WRITE TRIGGERS EVERY OTHER WRITE IN DB USING CLOUD FUNCTIONS)
                USER_RELATION_COLLECTION
            ).document(postOwnerId).collection(COMMENTS_COLLECTION).document(postId), "$commentId.commentContent" , commentContent
        )

        if (commentParentId == null) {
            batch.update(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentId), "commentContent" , commentContent

            )
        } else {
            batch.update(
                db.collection(POST_COLLECTION).document(postId).collection(COMMENTS_COLLECTION)
                    .document(commentParentId)
                    .collection(COMMENT_REPLY_COLLECTION).document(commentId),
                "commentContent" , commentContent
            )
        }
        Log.d(TAG, "editComment: Ran")
        batch.commit()
    }

    //TODO(DEFINE POST TYPE{PUBLIC,PRIVATE} AND GIVE """""READ ONLY SECURITY ACCESS""""" AS PER THAT AND FOLLOWER/FOLLOWING)
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

    //TODO(DEFINE POST TYPE{PUBLIC,PRIVATE} AND GIVE """""READ ONLY SECURITY ACCESS""""" AS PER THAT AND FOLLOWER/FOLLOWING)
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
                .orderBy("time", Query.Direction.ASCENDING)
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
                ).whereEqualTo("postId",postId).orderBy("time", Query.Direction.DESCENDING)
                .get().await().documents

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }


    suspend fun getPublicPosts(
        randomUser: String,
        docLimit: Long,
        docSnapshot: DocumentSnapshot?
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        val docs = if (docSnapshot != null)
            db.collection(USER_COLLECTION).document(randomUser).collection(USER_POST_COLLECTION)
                .whereEqualTo("public", true).orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit).startAfter(docSnapshot).get().await().documents
        else
            db.collection(USER_COLLECTION).document(randomUser).collection(USER_POST_COLLECTION)
                .whereEqualTo("public", true).orderBy("time", Query.Direction.DESCENDING)
                .limit(docLimit)
                .get().await().documents

        val retVal: ArrayList<DocumentSnapshot> = ArrayList()
        retVal.addAll(docs)
        return retVal
    }

    suspend fun getUserPosts(
        randomUser: String,
        docLimit: Long,
        docSnapshot: DocumentSnapshot?
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        val docs = if (docSnapshot != null)
            db.collection(USER_COLLECTION).document(randomUser).collection(USER_POST_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING).limit(docLimit)
                .startAfter(docSnapshot).get().await().documents
        else
            db.collection(USER_COLLECTION).document(randomUser).collection(USER_POST_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING).limit(docLimit)
                .startAfter(docSnapshot).get().await().documents

        val retVal: ArrayList<DocumentSnapshot> = ArrayList()
        retVal.addAll(docs)
        return retVal
    }


    fun deleteComment() {

    }

    fun bookMarkPost(
        userId:String,
        postId:String,
        postOwnerId:String,
        undoBookMark:Boolean
    ){
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        if(!undoBookMark){
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(bookMarkCountElement to FieldValue.increment(1)),
                SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(USER_POST_COLLECTION).document(postId),
                hashMapOf(bookMarkCountElement to FieldValue.increment(1)),
                SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(userId).collection(
                    USER_RELATION_COLLECTION
                ).document(postOwnerId).collection(BOOKMARK_COLLECTION).document(postId), hashMapOf(createdAtElement to FieldValue.serverTimestamp() , bookMarkCountElement to 1)
            )
            batch.set(
                db.collection(POST_COLLECTION).document(postId).collection(
                    BOOKMARK_COLLECTION
                ).document(userId), hashMapOf(createdAtElement to FieldValue.serverTimestamp() , bookMarkCountElement to 1 , userIdElement to userId)
            )
        }
        else{
            batch.set(
                db.collection(POST_COLLECTION).document(postId),
                hashMapOf(bookMarkCountElement to FieldValue.increment(-1)),
                SetOptions.merge()
            )
            batch.set(
                db.collection(USER_COLLECTION).document(postOwnerId).collection(USER_POST_COLLECTION).document(postId),
                hashMapOf(bookMarkCountElement to FieldValue.increment(-1)),
                SetOptions.merge()
            )
            batch.delete(
                db.collection(USER_COLLECTION).document(userId).collection(
                    USER_RELATION_COLLECTION
                ).document(postOwnerId).collection(BOOKMARK_COLLECTION).document(postId))
            batch.delete(
                db.collection(POST_COLLECTION).document(postId).collection(
                    BOOKMARK_COLLECTION
                ).document(userId)
            )
        }

        batch.commit()
    }


    suspend fun getAllLikes(
        postId: String,
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot>{
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                .orderBy(createdAtElement, Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(POST_COLLECTION).document(postId).collection(LIKES_COLLECTION)
                .orderBy(createdAtElement, Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    private const val TAG = "PostDb"

}