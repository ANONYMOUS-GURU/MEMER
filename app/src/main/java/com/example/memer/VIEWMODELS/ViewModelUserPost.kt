package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.MODELS.PostContents2
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelUserPost : ViewModel() {

    private var posts: ArrayList<PostContents2> = ArrayList()
    private val postsMutableLiveData: MutableLiveData<ArrayList<PostContents2>> =
        MutableLiveData<ArrayList<PostContents2>>()
    val postsLiveData: LiveData<ArrayList<PostContents2>>
        get() = postsMutableLiveData

    private val docLimit : Long = 40
    private var retVal:ArrayList<DocumentSnapshot> = ArrayList()
    private var moreDataPresent = false


    fun getUserPosts(userId:String){
        viewModelScope.launch {
            retVal = PostDb.getPostsUsers(null,docLimit,userId)
            moreDataPresent= retVal.size >= docLimit
            Log.d(TAG, "init block: ${retVal.size}")
            posts = ArrayList()
            retVal.forEach{
                posts.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main){
                postsMutableLiveData.value = posts
            }
            Log.d(TAG, "init block : Updated")
        }
    }
    fun getMoreUserPosts(userId:String){
        viewModelScope.launch {
            retVal = PostDb.getPostsUsers(retVal[retVal.size - 1],docLimit,userId)
            moreDataPresent= retVal.size >= docLimit
            retVal.forEach{
                posts.add(it.toPostContents2())
            }
            withContext(Dispatchers.Main){
                postsMutableLiveData.value = posts
            }
        }
    }

    companion object{
        private const val TAG = "ViewModelUserPost"
    }

}