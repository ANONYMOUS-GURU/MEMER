package com.example.memer.FIRESTORE

import android.util.Log

import com.example.memer.MODELS.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


object PostDb {

    private val CACHE = Source.CACHE
    private val SERVER = Source.SERVER

    fun removePost(post: PostContents2, userId: String) {
        val db = FirebaseFirestore.getInstance()
    }

    fun archivePost(post: PostContents2, userId: String) {

    }

    fun editPost(postCaption: String, postId: String) {

    }

    fun report(reports: Reports) {
        when (reports) {
            is Reports.CommentReport -> {
            }
            is Reports.PostReport -> {
            }
            is Reports.UserReport -> {
            }
        }
    }

    //TODO(EVERYTHING ON ADD POST INSIDE A CLOUD FUNCTION IN AWAIT FASHION AND FAIL IF NO INTERNET)
    fun addPost(
        imageReference: String,
        userId: String,
        username: String,
        userAvatarReference: String?,
        postDesc: String,
        postId: String,
    ): Task<Void> {


        val post = PostContents2(
            postId = postId,
            postOwnerId = userId,

            postResource = imageReference,
            postDescription = postDesc,
            postTypeImage = true,

            username = username,
            userAvatarReference = userAvatarReference,

            likeCount = 0,
            commentCount = 0,
            bookMarkCount = 0,
            reportCount = 0

        )

        val db = FirebaseFirestore.getInstance()
        val batch = FirebaseFirestore.getInstance().batch()
        batch.set(db.collection(FireStoreCollection.Post.value).document(postId), post)

        // SHOULD BE DONE BY A Cloud Function the incrementing part
        batch.set(
            db.collection(FireStoreCollection.User.value).document(userId),
            hashMapOf(UserElement.UserPostCount.value to FieldValue.increment(1)),
            SetOptions.merge()
        )

        return batch.commit()


    }

    //TODO(GET POSTS IMPLEMENT USING CLOUD FUNCTION FOR GETTING USER LIKES BOOKMARKS AND GETTING IT IN ACCORDANCE WITH USER RELATIONS ETC.)
    suspend fun getPosts(lastDocument: DocumentSnapshot?, docLimit: Long)
            : ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(FireStoreCollection.Post.value)
                .orderBy(PostElement.CreatedAt.value, Query.Direction.DESCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(FireStoreCollection.Post.value)
                .orderBy(PostElement.CreatedAt.value, Query.Direction.DESCENDING)
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
            db.collection(FireStoreCollection.Post.value)
                .whereEqualTo(PostElement.PostOwnerId.value, userId).orderBy(
                    PostElement.CreatedAt.value, Query.Direction.DESCENDING
                )
                .limit(docLimit).get().await().documents
        else
            db.collection(FireStoreCollection.Post.value)
                .whereEqualTo(PostElement.PostOwnerId.value, userId).orderBy(
                    PostElement.CreatedAt.value, Query.Direction.DESCENDING
                )
                .startAfter(lastDocument).limit(docLimit).get().await().documents

        Log.d(TAG, "init block: Got Posts ${mDocuments.size}")

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }
        return mData
    }


    fun updateLikes(
        mLike: Likes,
        incrementLike: Boolean
    ) {
        val db = FirebaseFirestore.getInstance()

        val batch = db.batch()
        // TODO(APART FROM USER_COLLECTION EVERYTHING ELSE SHOULD BE HANDLED BY CLOUD FUNCTION FOR SECURITY)
        if (incrementLike) {
            Log.d(TAG, "updateLikes: Incrementing Like")
            batch.set(db.collection(FireStoreCollection.Like.value).document(mLike.likeId), mLike)
            if (mLike.likeType == LikeType.CommentLike.value) {
                batch.update(
                    db.collection(FireStoreCollection.Comment.value).document(mLike.likeTypeId),
                    CommentElement.CommentLikeCount.value, FieldValue.increment(1)
                )
            } else {
                batch.update(
                    db.collection(FireStoreCollection.Post.value).document(mLike.likeTypeId),
                    PostElement.LikeCount.value, FieldValue.increment(1)
                )
            }
        } else {
            Log.d(TAG, "updateLikes: Decrementing Like")
            batch.delete(db.collection(FireStoreCollection.Like.value).document(mLike.likeId))
            if (mLike.likeType == LikeType.CommentLike.value) {
                batch.update(
                    db.collection(FireStoreCollection.Comment.value).document(mLike.likeTypeId),
                    CommentElement.CommentLikeCount.value, FieldValue.increment(-1)
                )
            } else {
                batch.update(
                    db.collection(FireStoreCollection.Post.value).document(mLike.likeTypeId),
                    PostElement.LikeCount.value, FieldValue.increment(-1)
                )
            }
        }

        // INSIDE CLOUD FUNCTION


        // ALSO ADD A FUNCTION TO INCREASE USER GLOBAL LIKES / CLAPS

        batch.commit()
    }

    // TODO(THIS FUNCTION WILL BE DELETED AS LIKES WILL BE RETRIEVED BY A CLOUD FUNCTION ALONG WITH GET POSTS)
    suspend fun getUserLikes(
        likeType: LikeType,
        likeTypeId: String,
        userId: String,
    ): Boolean {
        val db = FirebaseFirestore.getInstance()
        val userLike =
            db.collection(FireStoreCollection.Like.value)
                .whereEqualTo(LikeElement.LikeType.value, likeType.value)
                .whereEqualTo(LikeElement.UserId.value, userId)
                .whereEqualTo(LikeElement.LikeTypeId.value, likeTypeId)
                .get().await().documents.size

        return userLike != 0
    }

    suspend fun getUserBookMarks(
        postId: String,
        userId: String,
    ): Boolean {
        val db = FirebaseFirestore.getInstance()
        val userBookmark =
            db.collection(FireStoreCollection.BookMark.value)
                .whereEqualTo(BookMarkElement.UserId.value, userId).whereEqualTo(BookMarkElement.PostId.value, postId)
                .get().await().documents.size

        return userBookmark != 0
    }

    fun addCommentPost(
        comment: Comment,
    ) {
        val db = FirebaseFirestore.getInstance()

        val batch = db.batch()


        batch.set(
            db.collection(FireStoreCollection.Comment.value).document(comment.commentId),
            comment
        )

        // TODO(below calls can be by CLOUD FUNCTIONS)

        if (comment.commentParentId != null) {
            batch.update(
                db.collection(FireStoreCollection.Comment.value).document(comment.commentParentId),
                CommentElement.CommentReplyCount.value, FieldValue.increment(1)
            )
        }

        batch.update(
            db.collection(FireStoreCollection.Post.value).document(comment.commentPostId),
            PostElement.CommentCount.value, FieldValue.increment(1)
        )

        batch.commit()
    }

    fun editComment(
        commentContent: String,
        commentId: String
    ) {

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        batch.update(
            db.collection(FireStoreCollection.Comment.value)
                .document(commentId), CommentElement.CommentContent.value, commentContent
        )

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
            db.collection(FireStoreCollection.Comment.value).whereEqualTo(CommentElement.CommentPostId.value, postId)
                .whereEqualTo(CommentElement.CommentParentId.value,null)
                .orderBy(CommentElement.CreatedAt.value, Query.Direction.ASCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(FireStoreCollection.Comment.value).whereEqualTo(CommentElement.CommentPostId.value, postId)
                .orderBy(CommentElement.CreatedAt.value, Query.Direction.ASCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    //TODO(DEFINE POST TYPE{PUBLIC,PRIVATE} AND GIVE """""READ ONLY SECURITY ACCESS""""" AS PER THAT AND FOLLOWER/FOLLOWING)
    suspend fun getRepliesCommentsPost(
        commentParentId: String,
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(FireStoreCollection.Comment.value).whereEqualTo(CommentElement.CommentParentId.value, commentParentId)
                .orderBy(CommentElement.CreatedAt.value, Query.Direction.ASCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(FireStoreCollection.Comment.value).whereEqualTo(CommentElement.CommentParentId.value, commentParentId)
                .orderBy("time", Query.Direction.ASCENDING)
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
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        val mDocuments =
            db.collection(FireStoreCollection.Comment.value).whereEqualTo(CommentElement.CommentPostId.value, postId)
                .whereEqualTo(CommentElement.CommentOwnerId.value, userId)
                .orderBy(CommentElement.CreatedAt.value, Query.Direction.ASCENDING)
                .get().await().documents

        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    suspend fun getUserPosts(
        randomUser: String,
        docLimit: Long,
        docSnapshot: DocumentSnapshot?
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()
        val docs = if (docSnapshot == null)
            db.collection(FireStoreCollection.Post.value).whereEqualTo(PostElement.PostOwnerId.value, randomUser)
                .orderBy(PostElement.CreatedAt.value, Query.Direction.DESCENDING).limit(docLimit)
                .get().await().documents
        else
            db.collection(FireStoreCollection.Post.value).whereEqualTo(PostElement.PostOwnerId.value, randomUser)
                .orderBy(PostElement.CreatedAt.value, Query.Direction.DESCENDING).limit(docLimit)
                .startAfter(docSnapshot).get().await().documents

        val retVal: ArrayList<DocumentSnapshot> = ArrayList()
        retVal.addAll(docs)
        return retVal
    }

    fun bookMarkPost(
        bookMark: Bookmarks,
        undoBookMark: Boolean
    ) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        if (!undoBookMark) {
            batch.update(
                db.collection(FireStoreCollection.Post.value).document(bookMark.postId),
                PostElement.BookMarkCount.value, FieldValue.increment(1)
            )
            batch.set(
                db.collection(FireStoreCollection.BookMark.value).document(bookMark.bookmarkId),
                bookMark
            )
        } else {
            batch.update(
                db.collection(FireStoreCollection.Post.value).document(bookMark.postId),
                PostElement.BookMarkCount.value, FieldValue.increment(-1)
            )
            batch.delete(
                db.collection(FireStoreCollection.BookMark.value).document(bookMark.bookmarkId),
            )
        }

        batch.commit()
    }

    suspend fun getAllLikes(
        likeType: LikeType,
        likeTypeId: String,
        lastDocument: DocumentSnapshot?,
        docLimit: Long
    ): ArrayList<DocumentSnapshot> {
        val db = FirebaseFirestore.getInstance()

        val mDocuments = if (lastDocument == null)
            db.collection(FireStoreCollection.Like.value).whereEqualTo(LikeElement.LikeType.value, likeType.value)
                .whereEqualTo(LikeElement.LikeTypeId.value, likeTypeId)
                .orderBy(LikeElement.CreatedAt.value, Query.Direction.ASCENDING)
                .limit(docLimit).get().await().documents
        else
            db.collection(FireStoreCollection.Like.value).whereEqualTo(LikeElement.LikeType.value, likeType.value)
                .whereEqualTo(LikeElement.LikeTypeId.value, likeTypeId)
                .orderBy(LikeElement.CreatedAt.value, Query.Direction.ASCENDING)
                .startAfter(lastDocument).limit(docLimit).get().await().documents


        val mData = ArrayList<DocumentSnapshot>()
        mDocuments.forEach {
            mData.add(it)
        }

        return mData
    }

    private const val TAG = "PostDb"

}