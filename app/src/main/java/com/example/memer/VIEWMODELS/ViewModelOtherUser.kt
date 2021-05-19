package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.HELPERS.DataSource
import com.example.memer.MODELS.UserProfileInfo

class ViewModelOtherUser : ViewModel() {

    private var user: UserProfileInfo? = null
    private val userLiveData: MutableLiveData<UserProfileInfo?> = MutableLiveData<UserProfileInfo?>()

//    fun getOtherUser(userId:String): LiveData<UserProfileInfo?> {
//        user = DataSource.getFakeOtherUser()
//        userLiveData.value = user
//        return userLiveData
//    }

}