package com.example.memer.VIEWMODELS

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.HELPERS.MyUser
import com.example.memer.MODELS.UserEditableInfo
import com.example.memer.MODELS.UserNonEditInfo

class ViewModelUserInfo : ViewModel() {

    private var userEdit:UserEditableInfo = UserEditableInfo()
    private var userNonEditInfo:UserNonEditInfo = UserNonEditInfo()
    private var userImageReference:Pair<String?,String?> = null to null

    private val userEditMutableLiveData:MutableLiveData<UserEditableInfo> = MutableLiveData<UserEditableInfo>()
    private val userNonEditInfoMutableLiveData:MutableLiveData<UserNonEditInfo> = MutableLiveData<UserNonEditInfo>()
    private val userIdMutableLiveData:MutableLiveData<String?> = MutableLiveData<String?>()
    private val userImageReferenceMutableLiveData:MutableLiveData<Pair<String?,String?>> = MutableLiveData<Pair<String?,String?>>()

    val userEditLiveData:LiveData<UserEditableInfo>
        get() = userEditMutableLiveData

    val userNonEditInfoLiveData:LiveData<UserNonEditInfo>
        get() = userNonEditInfoMutableLiveData

    val userImageReferenceLiveData:LiveData<Pair<String?,String?>>
        get() = userImageReferenceMutableLiveData


    init {
        val mUser = MyUser.getUser() !!
        userEdit = getUserEditableInfo(mUser)
        userNonEditInfo = getUserNonEditInfo(mUser)
        userImageReference = getUserImageReference(mUser)

        userEditMutableLiveData.value = userEdit
        userNonEditInfoMutableLiveData.value = userNonEditInfo
        userImageReferenceMutableLiveData.value = userImageReference
    }

    private fun getUserEditableInfo(mUser:MyUser.UserData) : UserEditableInfo{
        return UserEditableInfo(
            nameOfUser = mUser.nameOfUser,
            username = mUser.username,
            bio = mUser.bio
        )
    }
    private fun getUserNonEditInfo(mUser:MyUser.UserData) : UserNonEditInfo{
        return UserNonEditInfo(
            followersCount = mUser.userFollowers.size,
            followingCount = mUser.userFollowing.size,
            postCount = mUser.postsReference.size
        )
    }
    private fun getUserImageReference(mUser: MyUser.UserData) : Pair<String?,String?>{
        return mUser.userProfilePicReference to mUser.userAvatarReference
    }




    fun updateUserEditInfo(_user:UserEditableInfo){
        UserDb.updateEditableInfo(_user)
        userEditMutableLiveData.value=_user
    }
    fun updateUserNonEditInfo(_user:UserNonEditInfo){
        userNonEditInfoMutableLiveData.value=_user
    }
    fun updateUserImageReference(_user:Pair<String?,String?>){
        UserDb.updateImageReference(_user)
        userImageReferenceMutableLiveData.value=_user

    }



}