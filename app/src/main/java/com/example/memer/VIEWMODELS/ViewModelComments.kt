package com.example.memer.VIEWMODELS

import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.Comment
import com.example.memer.MODELS.Comment.Companion.toComment
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


    init {
        getComments()
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

        val commentId = user.userId + System.currentTimeMillis() + "Comment"
        val comment = Comment(
            commentId,
            commentContent,
            commentParentId,
            user.userId,
            user.username,
            user.userAvatarReference,
            post.postOwnerId,
            post.postId,
            0
        )

        PostDb.addCommentPost(comment,post.postId, user.userId, post.postOwnerId,commentParentId)

        if(parentPosition == -1) {
            data.add(0, comment to ArrayList())
        }
        else {
            data[parentPosition].second.add(comment)
            data[parentPosition].first.commentReplyCount += 1
        }

        dataMLD.value = data
    }

    private val docLimitReplies = 8L

    fun getCommentsReplies(
        postId: String,
        commentParentId: String,
        position: Int
    ) {
        viewModelScope.launch {
            val docs = PostDb.getRepliesCommentsPost(postId,commentParentId,lastDocumentReplies[position],docLimitReplies)
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

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelCommentsFactory(
    private val post: PostContents2
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelComments(post) as T
}