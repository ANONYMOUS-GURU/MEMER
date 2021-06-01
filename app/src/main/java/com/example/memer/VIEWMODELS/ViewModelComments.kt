package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ViewModelComments : ViewModel() {

    private var data: ArrayList<Pair<Comment, ArrayList<Comment>>> = ArrayList()
    private val dataMLD: MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>> =
        MutableLiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>()
    val dataLD: LiveData<ArrayList<Pair<Comment, ArrayList<Comment>>>>
        get() = dataMLD

    private val lastDocumentReplies:ArrayList<DocumentSnapshot?> = ArrayList()


    private val lastSnapshot: DocumentSnapshot? = null
    private val docLimitComments = 15L
    fun getComments(post: PostContents2) {
        data = ArrayList()
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
        commentParentId: String? = null
    ) {
        if (user == null)
            return
        PostDb.addCommentPost(
            post.postId, user.userId, user.username, user.userAvatarReference,
            post.postOwnerId, commentContent, commentParentId
        )
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