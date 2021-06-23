package com.example.memer.VIEWMODELS

import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.example.memer.MODELS.PostHomePage
import com.example.memer.MODELS.UserProfileInfo
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

    private var isFollowing: Boolean = false
    private val isFollowingMLD: MutableLiveData<Boolean> = MutableLiveData()
    val isFollowingLD: LiveData<Boolean>
        get() = isFollowingMLD

    private var lastPostDocumentSnapshot: DocumentSnapshot? = null
    private var docLimit: Long = 20
    var moreDataPresent: Boolean = true

    init {
        viewModelScope.launch {
            data = UserDb.getRandomUser(randomUserId)
            isFollowing = UserDb.isFollowing(userId, randomUserId)
            withContext(Dispatchers.Main) {
                dataMLD.value = data
                isFollowingMLD.value = isFollowing
            }
            getInitPosts()
        }
    }

    private fun getInitPosts() {
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = if (isFollowing) {
                PostDb.getUserPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            } else {
                PostDb.getPublicPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            }
            moreDataPresent = docs.size >= docLimit
            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                post.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                postMLD.value = post
            }
        }
    }
    fun getMorePosts(){
        viewModelScope.launch {
            val docs: ArrayList<DocumentSnapshot> = if (isFollowing) {
                PostDb.getUserPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            } else {
                PostDb.getPublicPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            }
            moreDataPresent = docs.size >= docLimit
            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                post.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                postMLD.value = post
            }
        }
    }

    fun initListPost(userId: String) {
        postComplete = ArrayList()
        viewModelScope.launch {
            post.forEach {
                val userLike = PostDb.getUserLikes(
                    it.postId,
                    userId,
                    it.postOwnerId
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.postId,
                    userId,
                    it.postOwnerId
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
                postCompleteMLD.value = postComplete
            }

        }

    }
    fun getMoreListPost(userId: String) {
        viewModelScope.launch {
            val docs = PostDb.getPostsUsers(lastPostDocumentSnapshot, docLimit, userId)
            moreDataPresent = docs.size >= docLimit
            docs.forEach {
                post.add(it.toPostContents2())
                val userLike = PostDb.getUserLikes(
                    it.getString("postId")!!,
                    userId,
                    it.getString("postOwnerId")!!
                )
                // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
                val userBookMark = PostDb.getUserBookMarks(
                    it.getString("postId")!!,
                    userId,
                    it.getString("postOwnerId")!!
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
                postMLD.value = post
                postCompleteMLD.value = postComplete
            }
        }
    }

    fun likeClicked(
        position: Int,
        postId: String,
        userId: String,
        username: String,
        userAvatarReference: String?,
        nameOfUser:String,
        postOwnerId: String,
        incrementLike: Boolean
    ) {
//        val likes = LikedBy(username,userAvatarReference,userId,1,postId,postOwnerId,null)
        PostDb.updateLikesPost(
            postId,
            postOwnerId,
            userId,
            username,
            userAvatarReference,
            nameOfUser,
            incrementLike
        )

        postComplete[position].isLiked = postComplete[position].isLiked + if (incrementLike) 1 else -1
        postCompleteMLD.value = postComplete
    }

    fun bookMarkClicked(position: Int, userId: String, postId: String, postOwnerId: String) {

        if (postComplete[position].isBookmarked) {
            postComplete[position].isBookmarked = false
            PostDb.bookMarkPost(userId,postId,postOwnerId,true)
        } else {
            postComplete[position].isBookmarked = true
            PostDb.bookMarkPost(userId,postId,postOwnerId,false)
        }

        postCompleteMLD.value = postComplete
    }

    fun followUser() {
        if (isFollowing) {
            UserDb.unFollow(userId, randomUserId)
            isFollowing = false
            isFollowingMLD.value = isFollowing
            post = ArrayList()
            lastPostDocumentSnapshot = null
//            getMorePosts()
        } else {
            UserDb.follow(userId, randomUserId)

        }


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