package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.*
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelHomePagePost(userId: String) : ViewModel() {


    private var post: ArrayList<PostHomePage> = ArrayList()
    private var postMLD: MutableLiveData<ArrayList<PostHomePage>> =
        MutableLiveData<ArrayList<PostHomePage>>()
    val postLD: LiveData<ArrayList<PostHomePage>>
        get() = postMLD

    private var state:PostState = PostState.DataNotLoaded
    private var stateMLD: MutableLiveData<PostState> =
        MutableLiveData<PostState>()
    val stateLD: LiveData<PostState>
        get() = stateMLD

    var moreDataPresent: Boolean = false


    private val docLimit: Long = 2
    private var lastDocumentSnapshot: DocumentSnapshot? = null

    init {

        state = PostState.DataNotLoaded
        stateMLD.value = state


        post = ArrayList()
        postMLD.value = post

        moreDataPresent = false

        viewModelScope.launch {
            state = PostState.Loading
            withContext(Dispatchers.Main){
                stateMLD.value = state
            }
            val doc = PostDb.getPosts(lastDocumentSnapshot, docLimit)
            Log.d(TAG, "getData: Got Data")
            lastDocumentSnapshot = if (doc.size > 0) doc[doc.size - 1] else null
            doc.forEach {
                val userLike = PostDb.getUserLikes(
                    LikeType.PostLike,
                    it.getString("postId")!!,
                    userId
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.getString("postId")!!,
                    userId
                )
                post.add(
                    PostHomePage(
                        postContents = it.toPostContents2(),
                        isLiked = userLike,
                        isCommented = false,
                        isBookmarked = userBookMark
                    )
                )
            }
            withContext(Dispatchers.Main) {
                Log.d(TAG, "getData: updating")
                state = PostState.Loaded
                stateMLD.value = state
                postMLD.value = post
                moreDataPresent = doc.size >= docLimit
            }
        }
    }



    fun getMoreData(userId: String) {
        state = PostState.Loading
        stateMLD.value = state

        moreDataPresent = false
        viewModelScope.launch {
            val doc = PostDb.getPosts(
                lastDocumentSnapshot,
                docLimit
            )
            lastDocumentSnapshot = if (doc.size > 0) doc[doc.size - 1] else null

            doc.forEach {
                val userLike = PostDb.getUserLikes(
                    LikeType.PostLike,
                    it.getString("postId")!!,
                    userId
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.getString("postId")!!,
                    userId,
                )
                post.add(
                    PostHomePage(
                        postContents = it.toPostContents2(),
                        isLiked = userLike,
                        isCommented = false,
                        isBookmarked = userBookMark
                    )
                )
            }
            withContext(Dispatchers.Main) {
                state = PostState.Loaded
                stateMLD.value = state
                postMLD.value = post
                moreDataPresent = doc.size >= docLimit
            }
        }
    }


    fun likeClicked(
        position: Int,
        postId: String,
        userId: String,
        username: String,
        userAvatarReference: String?,
        nameOfUser: String,
        postOwnerId: String,
        incrementLike: Boolean
    ) {
        val mLike = Likes(
            likeId = userId + postId,
            userId = userId,
            username = username,
            nameOfUser = nameOfUser,
            userAvatarReference = userAvatarReference,
            likeType = LikeType.PostLike.value,
            likeTypeId = postId
        )
        PostDb.updateLikes(
            mLike = mLike,
            incrementLike
        )

        post[position].isLiked = !post[position].isLiked
        setData()
    }


    fun bookMarkClicked(
        position: Int,
        userId: String,
        postId: String,
        postOwnerId: String,
        postOwnerUsername: String,
        postOwnerAvatarReference: String?
    ) {

        val bookMark = Bookmarks(
            bookmarkId = userId + postId,
            postId = postId,
            userId = userId,
            postOwnerId = postOwnerId,
            postOwnerUsername = postOwnerUsername,
            postOwnerAvatarReference = postOwnerAvatarReference
        )
        if (post[position].isBookmarked) {
            post[position].isBookmarked = false
            PostDb.bookMarkPost(bookMark = bookMark, true)
        } else {
            post[position].isBookmarked = true
            PostDb.bookMarkPost(bookMark = bookMark, false)
        }

        setData()
    }


    private fun setData(){
        postMLD.value = post
    }

    companion object {
        private const val TAG = "ViewModelHomePagePost"
    }



}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelHomeFactory(
    private val userId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelHomePagePost(userId) as T
}