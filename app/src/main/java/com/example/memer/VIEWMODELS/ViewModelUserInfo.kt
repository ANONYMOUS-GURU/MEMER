package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.HELPERS.DataSource
import com.example.memer.MODELS.UserEditableInfo
import com.example.memer.MODELS.UserNonEditInfo
import com.example.memer.MODELS.UserProfileInfo

class ViewModelUserInfo : ViewModel() {

    private var userEdit:UserEditableInfo = UserEditableInfo()
    private var userNonEditInfo:UserNonEditInfo = UserNonEditInfo()
    private var userId : String? = null
    private var userImageReference:String? = null

    private val userEditLiveData:MutableLiveData<UserEditableInfo> = MutableLiveData<UserEditableInfo>()
    private val userNonEditInfoLiveData:MutableLiveData<UserNonEditInfo> = MutableLiveData<UserNonEditInfo>()
    private val userIdLiveData:MutableLiveData<String?> = MutableLiveData<String?>()
    private val userImageReferenceLiveData:MutableLiveData<String?> = MutableLiveData<String?>()

    init {
        userId = DataSource.getUserId()
        userEdit = DataSource.getFakeUserEditableInfo(userId)
        userNonEditInfo = DataSource.getFakeUserNonEditInfo(userId)
        userImageReference = DataSource.getUserImageReference(userId)

        userEditLiveData.value = userEdit
        userNonEditInfoLiveData.value = userNonEditInfo
        userIdLiveData.value = userId
        userImageReferenceLiveData.value = userImageReference
    }

    fun getUserEditInfo():LiveData<UserEditableInfo>{
        return userEditLiveData
    }

    fun updateUserEditInfo(_user:UserEditableInfo){
        userEditLiveData.value=_user
    }

    fun getUserNonEditInfo():LiveData<UserNonEditInfo>{
        return userNonEditInfoLiveData
    }

    fun updateUserNonEditInfo(_user:UserNonEditInfo){
        userNonEditInfoLiveData.value=_user
    }

    fun getUserImageReference():LiveData<String?>{
        return userImageReferenceLiveData
    }

    fun updateUserImageReference(_user:String?){
        userImageReferenceLiveData.value=_user
    }

    fun getUserId():String?{
        return userIdLiveData.value
    }

}