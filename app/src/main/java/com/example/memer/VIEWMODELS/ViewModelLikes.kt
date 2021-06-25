package com.example.memer.VIEWMODELS

import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.MODELS.Comment.Companion.toComment
import com.example.memer.MODELS.LikeType
import com.example.memer.MODELS.LikedBy
import com.example.memer.MODELS.LikedBy.Companion.toLikedBy
import com.example.memer.MODELS.PostContents2
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelLikes(private val post: PostContents2,private val userId:String) : ViewModel() {

    private val likeList = ArrayList<LikedBy>()
    private val likeListMLD = MutableLiveData<ArrayList<LikedBy>>()
    val likeListLD:LiveData<ArrayList<LikedBy>>
        get() = likeListMLD

    private var lastSnapshot: DocumentSnapshot? = null
    private val docLimit = 15L

    private var moreDataPresent = false
    val isMoreDataPresent:Boolean
        get() = moreDataPresent

    init {
        getLikes()
    }


    fun getLikes() {
        viewModelScope.launch {
            val docs = PostDb.getAllLikes(likeType = LikeType.PostLike , likeTypeId = post.postId, lastDocument = lastSnapshot, docLimit = docLimit)
            if(docs.size > 0 ){
                lastSnapshot = docs[docs.size - 1]
                moreDataPresent = docs.size.toLong() == docLimit
                docs.forEach {
                    val likedBy = it.toLikedBy()
                    likeList.add(likedBy !!)
                }
                withContext(Dispatchers.Main) {
                    likeListMLD.value = likeList
                }
            }
            else{
                moreDataPresent = false
            }
        }
    }


}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelLikesFactory(
    private val post: PostContents2,
    private val userId:String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelLikes(post,userId) as T
}