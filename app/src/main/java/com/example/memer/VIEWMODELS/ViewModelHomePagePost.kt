package com.example.memer.VIEWMODELS

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memer.HELPERS.DataSource
import com.example.memer.HELPERS.MyUser
import com.example.memer.MODELS.PostContents
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ViewModelHomePagePost : ViewModel() {

    private var mListData: ArrayList<PostContents> = ArrayList()
    private var _data: MutableLiveData<List<PostContents>> = MutableLiveData<List<PostContents>>()
    private var uid = Firebase.auth.currentUser?.uid

    init {
        mListData = DataSource.createHomePageDataSet()   // Implement this
        _data.value = mListData
    }

    fun getMoreData() {
        mListData.add(mListData[0])             // Implement this
        _data.value = mListData
    }

    fun getData(): LiveData<List<PostContents>> {
        return _data
    }

    fun likeClicked(position: Int){
        mListData[position].isLiked = !mListData[position].isLiked
        _data.value = mListData
//        updatePostContentsBookMarked(postId,userId)
    }

    fun bookMarkClicked(position: Int){
        mListData[position].isBookMarked = !mListData[position].isBookMarked
        _data.value = mListData
//        updatePostContentsBookMarked(postId,userId)
    }

    companion object {
        private const val TAG = "ViewModelHomePagePost"
    }

}