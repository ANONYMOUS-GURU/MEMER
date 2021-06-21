package com.example.memer.VIEWMODELS

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.MODELS.UserData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ViewModelUserInfo(initUser: UserData?, application: Application):ViewModel(){

    private var user: UserData? = null
    private var userMLD: MutableLiveData<UserData?> = MutableLiveData<UserData?>()
    val userLD : LiveData<UserData?>
        get() = userMLD

    private val appContext = application

    init {
        user = initUser
        userMLD.value  = user
    }

    fun updateUser(mUser: UserData){
        UserDb.updateUser(mUser)
        viewModelScope.launch {
            InternalStorage.writeUser(mUser, getApplication())

            withContext(Dispatchers.Main){
                user = mUser
                userMLD.value = user
            }
        }
    }

    private fun getApplication(): Context {
        return appContext
    }

    fun updateUserImageReference(_user: Pair<String?, String?>, userId: String){
        UserDb.updateImageReference(_user, userId)

        viewModelScope.launch {
            InternalStorage.updateUserImageReference(_user.first, _user.second, getApplication())

            withContext(Dispatchers.Main){
                user !! .userAvatarReference= _user.second
                user !! .userProfilePicReference = _user.first
                userMLD.value = user
            }
        }
    }

    fun initUser(){

        /*
         * Check if one needs to update the local user cache . Only applicable in case of non-user
         * based change of data or no connectivity for a very long time
         * */

        viewModelScope.launch {
            user = InternalStorage.readUser(getApplication())
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }
    }
    fun fetchAndReInitUser() {
        val mUser = Firebase.auth.currentUser !!
        viewModelScope.launch {
            user = UserDb.getOldUser(mUser)
            InternalStorage.writeUser(user!!, getApplication())
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }

    }
    fun userExists():Boolean{
        return InternalStorage.userExists(getApplication())
    }


    fun signOut(){
        Firebase.auth.signOut()
        FirebaseFirestore.getInstance().clearPersistence()
        InternalStorage.deleteUser(getApplication())
        user = null
        userMLD.value = null
    }

    companion object{
        private const val TAG = "VMUserInfo"
        private const val ACCESS_GOOGLE = "GOOGLE"
        private const val ACCESS_PHONE = "PHONE"
    }


}

// TODO(REMOVE THE WARNING)
@Suppress("UNCHECKED_CAST")
class ViewModelUserFactory(
    private val user: UserData?,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ViewModelUserInfo(user, application) as T
}
