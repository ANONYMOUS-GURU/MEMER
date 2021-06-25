package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostHomePage

class ViewModelEditPost(_post: PostContents2) : ViewModel() {

    private val post:PostContents2 = _post
    private val postMLD:MutableLiveData<PostContents2> = MutableLiveData<PostContents2>()
    val postLD: LiveData<PostContents2>
        get() = postMLD

    init {
        postMLD.value  = post
    }

    fun editPost(
        postId: String,
        postCaption:String
    ){
        PostDb.editPost(
            postCaption = postCaption,
            postId = postId
        )

        post.postDescription = postCaption
        postMLD.value = post

    }

}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelEditPostFactory(
    private val post: PostContents2
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelEditPost(post) as T
}