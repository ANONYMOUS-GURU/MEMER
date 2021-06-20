package com.example.memer.VIEWMODELS

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.MODELS.*
import com.example.memer.MODELS.PostContents2.Companion.toPostContents2
import com.example.memer.MODELS.UserData.Companion.toUserData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelUserInfo(application: Application) : AndroidViewModel(application) {

    private var user: UserData? = null
    private var userMLD: MutableLiveData<UserData?> = MutableLiveData<UserData?>()
    val userLD : LiveData<UserData?>
        get() = userMLD

    fun updateUser(mUser:UserData){
        UserDb.updateUser(mUser)
        viewModelScope.launch {
            InternalStorage.writeUser(mUser,getApplication())

            withContext(Dispatchers.Main){
                user = mUser
                userMLD.value = user
            }
        }
    }
    fun updateUserImageReference(_user:Pair<String?,String?>,userId:String){
        UserDb.updateImageReference(_user,userId)

        viewModelScope.launch {
            InternalStorage.updateUserImageReference(_user.first,_user.second,getApplication())

            withContext(Dispatchers.Main){
                user !! .userAvatarReference= _user.second
                user !! .userProfilePicReference = _user.first
                userMLD.value = user
            }
        }
    }

    fun initUser(mUser:FirebaseUser){

        /*
         * Check if one needs to update the local user cache . Only applicable in case of non-user
         * based change of data or no connectivity for a very long time
         * */

        val hasInternetConnectivity = false
        viewModelScope.launch {
             if(hasInternetConnectivity){
                 user = UserDb.getOldUser(mUser)
                 InternalStorage.writeUser(user !!,getApplication())
            }else{
                user = InternalStorage.readUser(getApplication())
            }
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }
    }
    fun fetchAndReInitUser() {
        val mUser = Firebase.auth.currentUser !!
        viewModelScope.launch {
            user = UserDb.getOldUser(mUser)
            InternalStorage.writeUser(user !!,getApplication())
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }

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