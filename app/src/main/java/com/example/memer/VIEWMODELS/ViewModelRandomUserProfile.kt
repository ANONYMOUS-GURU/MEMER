package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.MODELS.*
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelRandomUserProfile(private val userId: String, private val randomUserId: String) :
    ViewModel() {

    private lateinit var data: UserProfileInfo
    private val dataMLD: MutableLiveData<UserProfileInfo> = MutableLiveData()
    val dataLD: LiveData<UserProfileInfo>
        get() = dataMLD

    private var post: ArrayList<PostContents2> = ArrayList()
    private val postMLD: MutableLiveData<ArrayList<PostContents2>> = MutableLiveData()
    val postLD: LiveData<ArrayList<PostContents2>>
        get() = postMLD

    private var postComplete: ArrayList<PostHomePage> = ArrayList()
    private val postCompleteMLD: MutableLiveData<ArrayList<PostHomePage>> = MutableLiveData()
    val postCompleteLD: LiveData<ArrayList<PostHomePage>>
        get() = postCompleteMLD


    private var state:PostState = PostState.DataNotLoaded
    private var stateMLD: MutableLiveData<PostState> =
        MutableLiveData<PostState>()
    val stateLD: LiveData<PostState>
        get() = stateMLD

    private var stateListPost:PostState = PostState.DataNotLoaded
    private var stateListPostMLD: MutableLiveData<PostState> =
        MutableLiveData<PostState>()
    val stateListPostLD: LiveData<PostState>
        get() = stateListPostMLD


    private var lastPostDocumentSnapshot: DocumentSnapshot? = null
    private var docLimit: Long = 20
    var moreDataPresent: Boolean = false

    init {
        viewModelScope.launch {
            data = UserDb.getRandomUser(randomUserId)
            Log.d(TAG, "init: ${data.postCount} ")
            withContext(Dispatchers.Main) {
                dataMLD.value = data

                stateMLD.value = state
                stateListPostMLD.value = stateListPost

                postMLD.value = post
                postCompleteMLD.value = postComplete
            }
            getInitPosts()
        }
    }

    private fun getInitPosts() {
        moreDataPresent = false
        state = PostState.Loading
        stateMLD.value = state
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = PostDb.getUserPosts(
                randomUserId, docLimit, lastPostDocumentSnapshot
            )

            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                post.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {

                state = PostState.Loaded
                stateMLD.value = state

                postMLD.value = post
                moreDataPresent = docs.size >= docLimit
            }
        }
    }

    fun getMorePosts() {
        state = PostState.Loading
        stateMLD.value = state
        moreDataPresent = false
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = PostDb.getUserPosts(
                randomUserId, docLimit, lastPostDocumentSnapshot
            )

            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                post.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                state = PostState.Loaded
                stateMLD.value = state

                postMLD.value = post
                moreDataPresent = docs.size >= docLimit
            }
        }
    }

    fun initListPost(userId: String) {
        stateListPost = PostState.Loading
        stateListPostMLD.value = stateListPost
        viewModelScope.launch {
            post.forEach {
                val userLike = PostDb.getUserLikes(
                    LikeType.PostLike,
                    it.postId,
                    userId
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.postId,
                    userId
                )
                postComplete.add(
                    PostHomePage(
                        postContents = it,
                        isLiked = userLike,
                        isCommented = false,
                        isBookmarked = userBookMark
                    )
                )
            }
            withContext(Dispatchers.Main) {
                stateListPost = PostState.Loaded
                stateListPostMLD.value = stateListPost
                postCompleteMLD.value = postComplete
            }

        }

    }

    fun getMoreListPost(userId: String) {
        state = PostState.Loading
        stateMLD.value = state
        stateListPost = PostState.Loading
        stateListPostMLD.value = stateListPost

        moreDataPresent = false
        viewModelScope.launch {
            val docs = PostDb.getPostsUsers(lastPostDocumentSnapshot, docLimit, userId)

            docs.forEach {
                post.add(it.toPostContents2())
                val userLike = PostDb.getUserLikes(
                    LikeType.PostLike,
                    it.getString("postId")!!,
                    userId,
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.getString("postId")!!,
                    userId,
                )
                postComplete.add(
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

                stateListPost = PostState.Loaded
                stateMLD.value = stateListPost

                postMLD.value = post
                postCompleteMLD.value = postComplete

                moreDataPresent = docs.size >= docLimit
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
        PostDb.updateLikes(mLike = mLike, incrementLike = incrementLike)

        postComplete[position].isLiked = !postComplete[position].isLiked
        postCompleteMLD.value = postComplete
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

        if (postComplete[position].isBookmarked) {
            postComplete[position].isBookmarked = false
            PostDb.bookMarkPost(bookMark = bookMark, undoBookMark = true)
        } else {
            postComplete[position].isBookmarked = true
            PostDb.bookMarkPost(bookMark = bookMark, undoBookMark = false)
        }

        postCompleteMLD.value = postComplete
    }

    fun editPost(
        postId: String,
        postCaption: String,
        position: Int
    ) {
        PostDb.editPost(
            postCaption = postCaption,
            postId = postId
        )

        postComplete[position].postContents.postDescription = postCaption
        postCompleteMLD.value = postComplete

    }



    companion object {
        const val TAG = "VMRandomUserProfile"
    }

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelRandomUserProfileFactory(
    private val userId: String,
    private val randomUserId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelRandomUserProfile(userId, randomUserId) as T
}