package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.example.memer.MODELS.PostHomePage
import com.example.memer.MODELS.UserData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelHomePagePost : ViewModel() {

    private var post: ArrayList<PostHomePage> = ArrayList()
    private var postMLD: MutableLiveData<ArrayList<PostHomePage>> =
        MutableLiveData<ArrayList<PostHomePage>>()
    val postLD: LiveData<ArrayList<PostHomePage>>
        get() = postMLD

    var moreDataPresent: Boolean = false
    private var retVal: ArrayList<DocumentSnapshot> = ArrayList()
    private val docLimit: Long = 5


    fun getData(userId: String) {
        viewModelScope.launch {
            retVal = PostDb.getPosts(null, docLimit)
            moreDataPresent = retVal.size >= docLimit
            post = ArrayList()
            retVal.forEach {
                val userDisplayInfo = UserDb.getUserDisplayInfo(it.getString("postOwnerId")!!)
                val userLike = PostDb.getUserLikes(
                    it.getString("postId")!!,
                    userId,
                    it.getString("postOwnerId")!!
                )
                post.add(
                    PostHomePage(
                        postContents = it.toPostContents2(),
                        username = userDisplayInfo.username,
                        userAvatarReference = userDisplayInfo.userAvatarReference,
                        likeCount = userLike,
                        isCommented = false,
                        isBookmarked = false
                    )
                )
            }
            withContext(Dispatchers.Main) {
                postMLD.value = post
            }
        }
    }
    fun getMoreData(userId: String) {
        viewModelScope.launch {
            retVal = PostDb.getPosts(null, docLimit)
            moreDataPresent = retVal.size >= docLimit
            retVal.forEach {
                val userDisplayInfo = UserDb.getUserDisplayInfo(it.getString("postOwnerId")!!)
                val userLike = PostDb.getUserLikes(
                    it.getString("postId")!!,
                    userId,
                    it.getString("postOwnerId")!!
                )
                post.add(
                    PostHomePage(
                        postContents = it.toPostContents2(),
                        username = userDisplayInfo.username,
                        userAvatarReference = userDisplayInfo.userAvatarReference,
                        likeCount = userLike,
                        isCommented = false,
                        isBookmarked = false
                    )
                )
            }
            withContext(Dispatchers.Main) {
                postMLD.value = post
            }
        }
    }


    fun likeClicked(
        position: Int,
        postId: String,
        userId: String,
        postOwnerId: String,
        incrementLike: Boolean
    ) {
        PostDb.updateLikesPost(postId, userId, postOwnerId, incrementLike)

        post[position].likeCount = post[position].likeCount + if (incrementLike) 1 else -1
        postMLD.value = post
    }
//
//    fun bookMarkClicked(position: Int){
//        mListData[position].isBookMarked = !mListData[position].isBookMarked
//        _data.value = mListData
////        updatePostContentsBookMarked(postId,userId)
//    }

    companion object {
        private const val TAG = "ViewModelHomePagePost"
    }

}