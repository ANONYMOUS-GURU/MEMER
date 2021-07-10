package com.example.memer.VIEWMODELS

import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.LikeType
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.example.memer.MODELS.PostHomePage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VMSinglePost(private val postId:String,private val sharedBy:String?,private val userId:String) : ViewModel() {

    private lateinit var post:PostHomePage
    private var postMLD:MutableLiveData<PostHomePage> = MutableLiveData()
    val postLD:LiveData<PostHomePage>
        get() = postMLD

    init {
        viewModelScope.launch {
            val postContent = PostDb.getSinglePost(postId).toPostContents2()
            val userLike = PostDb.getUserLikes(
                likeType = LikeType.PostLike,
                likeTypeId = postContent.postId,
                userId = userId
            )
            // TODO(This Has To be local as bookmarks saved locally {postId saved locally and then restored if true}
            val userBookMark = PostDb.getUserBookMarks(
                postId = postContent.postId,
                userId = userId
            )
            post= PostHomePage(postContents = postContent, isLiked = userLike, isCommented = false,
                    isBookmarked = userBookMark)

            withContext(Dispatchers.Main){
                postMLD.value = post
            }
        }
    }

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class VMSinglePostFactory(
    private val postId: String,
    private val sharedBy: String?,
    private val userId: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        VMSinglePost(postId = postId,sharedBy = sharedBy,userId = userId) as T
}