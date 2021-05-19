package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.HELPERS.DataSource
import com.example.memer.MODELS.PostThumbnail
import com.example.memer.MODELS.UserPosts
import com.example.memer.MODELS.UserProfileInfo

class ViewModelUserPagePosts : ViewModel() {

    private var posts: ArrayList<PostThumbnail> = ArrayList()
    private val postsLiveData: MutableLiveData<ArrayList<PostThumbnail>> =
        MutableLiveData<ArrayList<PostThumbnail>>()

    init {
        posts = DataSource.createUserPagePosts()
        postsLiveData.value = posts
    }

    fun getPosts(userId: String): LiveData<ArrayList<PostThumbnail>> {
        return postsLiveData
    }

    fun addPosts(userId:String,postReferences:String){

    }

    fun deletePosts(userId:String,postId:String){

        // TODO("implement delete in cloud")
        var i=0
        while(i<posts.size){
            if(posts[i].postId == postId){
                posts.remove(posts[i])
                break
            }
            i+=1
        }

        postsLiveData.value = posts

    }
}