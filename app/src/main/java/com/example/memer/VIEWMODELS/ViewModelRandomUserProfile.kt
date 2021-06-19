package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
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
            val docs: ArrayList<DocumentSnapshot> = if (isFollowing) {
                PostDb.getUserPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            } else {
                PostDb.getPublicPosts(randomUserId, docLimit, lastPostDocumentSnapshot)
            }
            Log.d(TAG, "init: ${docs.size}")
            moreDataPresent = docs.size >= docLimit
            lastPostDocumentSnapshot = if (docs.size == 0) null else docs.last()
            docs.forEach {
                post.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main) {
                dataMLD.value = data
                postMLD.value = post
                isFollowingMLD.value = isFollowing
            }
        }
    }

    fun getPosts() {
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