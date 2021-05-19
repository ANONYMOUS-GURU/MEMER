package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.HELPERS.DataSource
import com.example.memer.MODELS.PostContents
import java.text.FieldPosition

class ViewModelHomePagePost : ViewModel() {

    private var mListData: ArrayList<PostContents> = ArrayList()
    private var _data: MutableLiveData<List<PostContents>> = MutableLiveData<List<PostContents>>()

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

}