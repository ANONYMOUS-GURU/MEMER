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

    private val dataPostThumbnail: ArrayList<PostContents2> = ArrayList()
    private var post: PostThumbnailState = PostThumbnailState.InitialLoading
    private val postMLD: MutableLiveData<PostThumbnailState> = MutableLiveData()
    val postLD: LiveData<PostThumbnailState>
        get() = postMLD

    private var dataPostComplete: ArrayList<PostHomePage> = ArrayList()
    private var postComplete: PostState = PostState.InitialLoading
    private val postCompleteMLD: MutableLiveData<PostState> = MutableLiveData()
    val postCompleteLD: LiveData<PostState>
        get() = postCompleteMLD


    private var lastPostDocumentSnapshot: DocumentSnapshot? = null
    private var docLimit: Long = 20
    var moreDataPresent: Boolean = false

    init {
        viewModelScope.launch {
            data = UserDb.getRandomUser(randomUserId)
            Log.d(TAG, "init: ${data.postCount} ")
            withContext(Dispatchers.Main) {
                dataMLD.value = data
                postMLD.value = post
                postCompleteMLD.value = postComplete
            }
            getInitPosts()
        }
    }

    private fun getInitPosts() {
        moreDataPresent = false
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = PostDb.getUserPosts(
                randomUserId, docLimit, lastPostDocumentSnapshot
            )


            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                dataPostThumbnail.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                post = PostThumbnailState.Loaded(dataPostThumbnail)
                postMLD.value = post
                moreDataPresent = docs.size >= docLimit
            }
        }
    }

    fun getMorePosts() {
        post = PostThumbnailState.LoadingMoreData(dataPostThumbnail)
        postMLD.value = post
        moreDataPresent = false
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = PostDb.getUserPosts(
                randomUserId, docLimit, lastPostDocumentSnapshot
            )

            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                dataPostThumbnail.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                post = PostThumbnailState.Loaded(dataPostThumbnail)
                postMLD.value = post
                moreDataPresent = docs.size >= docLimit
            }
        }
    }

    fun initListPost(userId: String) {
        viewModelScope.launch {
            dataPostThumbnail.forEach {
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
                dataPostComplete.add(
                    PostHomePage(
                        postContents = it,
                        isLiked = userLike,
                        isCommented = false,
                        isBookmarked = userBookMark
                    )
                )
            }
            withContext(Dispatchers.Main) {
                postComplete = PostState.Loaded(dataPostComplete)
                postCompleteMLD.value = postComplete
            }

        }

    }

    fun getMoreListPost(userId: String) {
        post = PostThumbnailState.LoadingMoreData(dataPostThumbnail)
        postMLD.value = post

        postComplete = PostState.LoadingMoreData(dataPostComplete)
        postCompleteMLD.value = postComplete
        moreDataPresent = false
        viewModelScope.launch {
            val docs = PostDb.getPostsUsers(lastPostDocumentSnapshot, docLimit, userId)

            docs.forEach {
                dataPostThumbnail.add(it.toPostContents2())
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
                dataPostComplete.add(
                    PostHomePage(
                        postContents = it.toPostContents2(),
                        isLiked = userLike,
                        isCommented = false,
                        isBookmarked = userBookMark
                    )
                )
            }
            withContext(Dispatchers.Main) {
                post = PostThumbnailState.Loaded(dataPostThumbnail)
                postComplete = PostState.Loaded(dataPostComplete)

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

        dataPostComplete[position].isLiked = !dataPostComplete[position].isLiked
        postComplete = PostState.Loaded(dataPostComplete)
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

        if (dataPostComplete[position].isBookmarked) {
            dataPostComplete[position].isBookmarked = false
            PostDb.bookMarkPost(bookMark = bookMark, undoBookMark = true)
        } else {
            dataPostComplete[position].isBookmarked = true
            PostDb.bookMarkPost(bookMark = bookMark, undoBookMark = false)
        }

        postComplete = PostState.Loaded(dataPostComplete)
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

        dataPostComplete[position].postContents.postDescription = postCaption
        postComplete = PostState.Loaded(dataPostComplete)
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