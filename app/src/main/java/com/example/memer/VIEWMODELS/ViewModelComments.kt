package com.example.memer.VIEWMODELS

import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.Comment
import com.example.memer.MODELS.Comment.Companion.toComment
import com.example.memer.MODELS.CommentState
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.UserData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition

class ViewModelComments(private val post: PostContents2) : ViewModel() {

    private var data: ArrayList<Pair<Comment, ArrayList<Comment>>> = ArrayList()
    private val dataMLD: MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>> =
        MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>()
    val dataLD: LiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>
        get() = dataMLD

    private val lastDocumentReplies:ArrayList<DocumentSnapshot?> = ArrayList()
    private val lastSnapshot: DocumentSnapshot? = null
    private val docLimitComments = 15L
    private val docLimitReplies = 8L

    private var commentState:CommentState = CommentState.Default
    private val commentStateMLD = MutableLiveData<CommentState>()
    val commentStateLD:LiveData<CommentState>
        get() = commentStateMLD



    init {
        getComments()
        commentStateMLD.value = commentState
    }

    // TODO(Implement get more in this -- NOT YET DONE as moreDataPresent not defined define and initialize acc. see VMLikes)
    fun getComments() {
        viewModelScope.launch {
            val docs = PostDb.getCommentPost(post.postId, lastSnapshot, docLimitComments)
            docs.forEach {
                val comment = it.toComment()
                if (comment != null) {
                    data.add(comment to ArrayList())
                    lastDocumentReplies.add(null)
                }
            }
            withContext(Dispatchers.Main) {
                dataMLD.value = data
            }
        }
    }

    fun addCommentPost(
        post: PostContents2,
        user: UserData?,
        commentContent: String,
        commentParentId: String? = null,
        parentPosition : Int = -1
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

        if(parentPosition == -1) {
            data.add(0, comment to ArrayList())
        }
        else {
            data[parentPosition].second.add(comment)
            data[parentPosition].first.commentReplyCount += 1
        }

        dataMLD.value = data
    }



    fun getCommentsReplies(
        postId: String,
        commentParentId: String,
        position: Int
    ) {
        viewModelScope.launch {
            val docs = PostDb.getRepliesCommentsPost(commentParentId,lastDocumentReplies[position],docLimitReplies)
            lastDocumentReplies[position] = docs[docs.size-1]
            docs.forEach {
                val comment = it.toComment()
                if (comment != null) {
                    data[position].second.add(comment)
                }
            }
            withContext(Dispatchers.Main) {
                dataMLD.value = data
            }
        }
    }

    fun editComment(
        postId: String,
        userId: String,
        postOwnerId: String,
        commentContent: String,
        commentParentId: String? = null,
        commentId:String,
        parentPosition: Int,
        position: Int
    ){
        PostDb.editComment(commentContent, commentId)
        if(commentParentId == null){
            data[position].first.commentContent = commentContent
        }else{
            data[parentPosition].second[position].commentContent = commentContent
        }
        dataMLD.value = data
    }


    fun onReplyClick(userId: String,username:String,commentParentId: String,parentIndex:Int){
        commentState = CommentState.ReplyTo(userId,username,commentParentId,parentIndex)
        commentStateMLD.value = commentState
    }
    fun onEditClick(position:Int,parentIndex:Int){
        commentState = CommentState.Edit(position,parentIndex)
        commentStateMLD.value = commentState
    }
    fun makeCommentDefault(){
        commentState = CommentState.Default
        commentStateMLD.value = commentState
    }

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelCommentsFactory(
    private val post: PostContents2
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelComments(post) as T
}