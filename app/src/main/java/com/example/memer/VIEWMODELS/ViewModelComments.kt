package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.*
import com.example.memer.MODELS.Comment.Companion.toComment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition

class ViewModelComments(private val post: PostContents2,private val userId:String) : ViewModel() {

    private var data: ArrayList<Pair<Comment, ArrayList<Comment>>> = ArrayList()
    private val dataMLD: MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>> =
        MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>()
    val dataLD: LiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>
        get() = dataMLD


    private var lastSnapshot: DocumentSnapshot? = null
    private val docLimitComments = 15L
    var moreDataPresent: Boolean = false

    private val docLimitReplies = 15L
    private val lastDocumentReplies: ArrayList<DocumentSnapshot?> = ArrayList()

    private var commentState: CommentState = CommentState.Default
    private val commentStateMLD = MutableLiveData<CommentState>()
    val commentStateLD: LiveData<CommentState>
        get() = commentStateMLD

    private var commentDataState: CommentDataState = CommentDataState.DataNotLoaded
    private val commentDataStateMLD = MutableLiveData<CommentDataState>()
    val commentDataStateLD: LiveData<CommentDataState>
        get() = commentDataStateMLD

    private var commentReplyDataState: ArrayList<CommentReplyDataState> = ArrayList()
    private val commentReplyDataStateMLD = MutableLiveData<ArrayList<CommentReplyDataState>>()
    val commentReplyDataStateLD: LiveData<ArrayList<CommentReplyDataState>>
        get() = commentReplyDataStateMLD


    init {
        commentDataStateMLD.value = commentDataState
        commentStateMLD.value = commentState

        moreDataPresent = false


        viewModelScope.launch {
            commentDataState = CommentDataState.Loading
            withContext(Dispatchers.Main){
                commentDataStateMLD.value = commentDataState
            }
            data = ArrayList()
            val docs = PostDb.getCommentPost(post.postId, lastSnapshot, docLimitComments)
            lastSnapshot = if (docs.isNotEmpty()) docs.last() else lastSnapshot
            docs.forEach {
                val comment = it.toComment()
                if (comment != null) {
                    comment.isLiked = PostDb.getUserLikes(LikeType.CommentLike,comment.commentId,userId)
                    data.add(comment to ArrayList())
                    Log.d(TAG, "init: added -> $comment")
                    lastDocumentReplies.add(null)
                    commentReplyDataState.add(CommentReplyDataState.Default)
                }
            }
            withContext(Dispatchers.Main) {
                commentDataState = CommentDataState.Loaded
                commentDataStateMLD.value = commentDataState
                Log.d(TAG, "init: ${data.size}")
                commentReplyDataStateMLD.value = commentReplyDataState
                dataMLD.value = data

                moreDataPresent = docs.size >= docLimitComments
            }
        }
    }

    // TODO(Implement get more in this -- NOT YET DONE as moreDataPresent not defined define and initialize acc. see VMLikes)
    fun getComments() {
        moreDataPresent = false
        commentDataState = CommentDataState.Loading
        commentDataStateMLD.value = commentDataState
        viewModelScope.launch {
            val docs = PostDb.getCommentPost(post.postId, lastSnapshot, docLimitComments)
            lastSnapshot = if (docs.isNotEmpty()) docs.last() else lastSnapshot
            docs.forEach {
                val comment = it.toComment()
                if (comment != null) {
                    comment.isLiked = PostDb.getUserLikes(LikeType.CommentLike,comment.commentId,userId)
                    data.add(comment to ArrayList())
                    lastDocumentReplies.add(null)
                    commentReplyDataState.add(CommentReplyDataState.Default)
                }
            }
            withContext(Dispatchers.Main) {
                Log.d(TAG, "getComments: $moreDataPresent")
                commentDataState = CommentDataState.Loaded
                commentDataStateMLD.value = commentDataState
                commentReplyDataStateMLD.value = commentReplyDataState
                dataMLD.value = data

                moreDataPresent = docs.size >= docLimitComments
            }
        }
    }

    fun addCommentPost(
        post: PostContents2,
        user: UserData?,
        commentContent: String,
        commentParentId: String? = null,
        parentPosition: Int = -1
    ) {
        if (user == null)
            return

        val commentId = user.userId + System.currentTimeMillis() + post.postId
        val comment = Comment(
            commentId = commentId, commentContent = commentContent,
            commentParentId = commentParentId, commentOwnerId = user.userId,
            commentOwnerUsername = user.username, commentOwnerUserAvatar = user.userAvatarReference,
            commentPostOwnerId = post.postOwnerId, commentPostId = post.postId
        )

        PostDb.addCommentPost(comment)

        if (parentPosition == -1) {
            data.add(comment to ArrayList())
            commentReplyDataState.add(CommentReplyDataState.Default)
            dataMLD.value = data
            commentReplyDataStateMLD.value = commentReplyDataState
        } else {
            data[parentPosition].second.add(comment)
            dataMLD.value = data
        }
    }


    fun getCommentsReplies(
        commentParentId: String,
        position: Int
    ) {
        commentReplyDataState[position] = CommentReplyDataState.Loading
        commentReplyDataStateMLD.value = commentReplyDataState
        viewModelScope.launch {
            val docs = PostDb.getRepliesCommentsPost(
                commentParentId,
                lastDocumentReplies[position],
                docLimitReplies
            )
            lastDocumentReplies[position] = if (docs.isNotEmpty()) docs.last() else lastDocumentReplies[position]
            docs.forEach {
                val comment = it.toComment()
                if (comment != null) {
                    comment.isLiked = PostDb.getUserLikes(LikeType.CommentLike,comment.commentId,userId)
                    data[position].second.add(comment)
                }
            }
            withContext(Dispatchers.Main) {
                data[position].first.commentReplyCount -= docs.size
                dataMLD.value = data
                commentReplyDataState[position] = CommentReplyDataState.Loaded
                commentReplyDataStateMLD.value = commentReplyDataState
            }
        }
    }

    fun editComment(
        commentContent: String,
        commentParentId: String? = null,
        commentId: String,
        parentPosition: Int,
        position: Int
    ) {
        PostDb.editComment(commentContent, commentId)
        if (commentParentId == null) {
            data[position].first.commentContent = commentContent
        } else {
            data[parentPosition].second[position].commentContent = commentContent
        }
        dataMLD.value = data
    }


    fun onReplyClick(username: String, commentParentId: String, parentIndex: Int) {
        commentState = CommentState.ReplyTo(userId, username, commentParentId, parentIndex)
        commentStateMLD.value = commentState
    }

    fun onEditClick(position: Int, parentIndex: Int) {
        commentState = CommentState.Edit(position, parentIndex)
        commentStateMLD.value = commentState
    }

    fun makeCommentDefault() {
        commentState = CommentState.Default
        commentStateMLD.value = commentState
    }

    fun likeComment(
        commentId: String,
        username: String,
        userAvatarReference: String?,
        nameOfUser: String,
        position: Int,
        parentPosition: Int
    ) {
        val likeId = userId + commentId

        val mLike = Likes(
            likeId = likeId,
            userId = userId,
            username = username,
            nameOfUser = nameOfUser,
            userAvatarReference = userAvatarReference,
            likeType = LikeType.CommentLike.value,
            likeTypeId = commentId
        )
        if (parentPosition == -1) {
            if (data[position].first.isLiked) {
                PostDb.updateLikes(mLike = mLike, incrementLike = false)
            } else {
                PostDb.updateLikes(mLike = mLike, incrementLike = true)
            }
            data[position].first.isLiked = !data[position].first.isLiked
        } else {
            if (data[parentPosition].second[position].isLiked) {
                PostDb.updateLikes(mLike = mLike, incrementLike = false)
            } else {
                PostDb.updateLikes(mLike = mLike, incrementLike = true)
            }
            data[parentPosition].second[position].isLiked =
                !data[parentPosition].second[position].isLiked
        }

        dataMLD.value = data


    }

    companion object {
        private const val TAG = "VMComments"
    }

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelCommentsFactory(
    private val post: PostContents2,
    private val userId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelComments(post,userId) as T
}