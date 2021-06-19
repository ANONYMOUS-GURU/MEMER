package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.example.memer.MODELS.PostHomePage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelHomePagePost : ViewModel() {

    private var post: ArrayList<PostHomePage> = ArrayList()
    private var postMLD: MutableLiveData<ArrayList<PostHomePage>> =
        MutableLiveData<ArrayList<PostHomePage>>()
    val postLD: LiveData<ArrayList<PostHomePage>>
        get() = postMLD

    var moreDataPresent: Boolean = true
    private var retVal: ArrayList<DocumentSnapshot> = ArrayList()
    private val docLimit: Long = 5
    private var lastDocumentSnapshot: DocumentSnapshot? = null

    fun getData(
        userId: String,
        onCompleteListener: OnCompleteListener<QuerySnapshot>,
        onFailureListener: OnFailureListener
    ) {
        lastDocumentSnapshot = null
        post = ArrayList()
        viewModelScope.launch {
            retVal = PostDb.getPosts(
                lastDocumentSnapshot,
                docLimit,
                onCompleteListener,
                onFailureListener
            )
            if (retVal.size > 0) {
                Log.d(TAG, "getData: Got Data")
                moreDataPresent = retVal.size >= docLimit
                lastDocumentSnapshot = retVal[retVal.size - 1]
                retVal.forEach {
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
                    Log.d(TAG, "getData: converting")
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
                    postMLD.value = post
                }
            }else{
                moreDataPresent = false
            }


        }
    }

    fun getMoreData(
        userId: String,
        onCompleteListener: OnCompleteListener<QuerySnapshot>,
        onFailureListener: OnFailureListener
    ) {
        viewModelScope.launch {
            retVal = PostDb.getPosts(
                lastDocumentSnapshot,
                docLimit,
                onCompleteListener,
                onFailureListener
            )
            lastDocumentSnapshot = retVal[retVal.size - 1]
            moreDataPresent = retVal.size >= docLimit
            retVal.forEach {
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
                postMLD.value = post
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

        post[position].isLiked = post[position].isLiked + if (incrementLike) 1 else -1
        postMLD.value = post
    }


    fun bookMarkClicked(position: Int, userId: String, postId: String, postOwnerId: String) {

        if (post[position].isBookmarked) {
            post[position].isBookmarked = false
            PostDb.bookMarkPost(userId,postId,postOwnerId,true)
        } else {
            post[position].isBookmarked = true
            PostDb.bookMarkPost(userId,postId,postOwnerId,false)
        }

        postMLD.value = post
    }

    companion object {
        private const val TAG = "ViewModelHomePagePost"
    }

}