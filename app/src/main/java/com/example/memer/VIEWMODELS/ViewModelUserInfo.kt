package com.example.memer.VIEWMODELS

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.PostDb
import com.example.memer.FIRESTORE.UserDb
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
        user = mUser
        userMLD.value = user
    }
    fun updateUserImageReference(_user:Pair<String?,String?>,userId:String){
        UserDb.updateImageReference(_user,userId)
        user !! .userAvatarReference= _user.second
        user !! .userProfilePicReference = _user.first

        userMLD.value =user

    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = task.result?.user
                val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                if(user!=null){
                    if(isNewUser)
                        createNewUserInDb(user, ACCESS_PHONE)
                    else {
                        Log.d(TAG, "firebaseAuthWithGoogle: Start getting user data")
                        authenticateUser(user)
                    }
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplication(), "Invalid Verification Code", Toast.LENGTH_SHORT).show()
                }
                user = null
                userMLD.value = user
            }
        }
    }
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                    if(user!=null){
                        if(isNewUser)
                            createNewUserInDb(user, ACCESS_GOOGLE)
                        else {
                            Log.d(TAG, "firebaseAuthWithGoogle: Start getting user data")
                            authenticateUser(user)
                        }
                    }

                } else {
                    user = null
                    userMLD.value = user
                }
            }
    }
    private fun createNewUserInDb(user: FirebaseUser?, signInType:String){
        if (user != null) {
            val name = user.displayName
            if (name != null) {
                createNewUser(
                    UserBasicInfo(
                        userId = user.uid,
                        nameOfUser = name,
                        username = user.uid,
                        signInType = signInType,
                        phoneNumber = null,
                    ).toUserData()
                )
            } else {
                createNewUser(
                    UserBasicInfo(
                        userId = user.uid,
                        nameOfUser = (user.uid.subSequence(0, 5)).toString(),
                        username = user.uid,
                        signInType = signInType,
                        phoneNumber = null,
                    ).toUserData()
                )
            }
        }
    }
    private fun createNewUser(userData: UserData) {
        viewModelScope.launch {
            UserDb.addNewUser(userData)
            user = userData
            user!!.isAuthenticated = true
            user!!.isNewUser = true
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }
    }
    private fun authenticateUser(mUser: FirebaseUser) {
        viewModelScope.launch {
            user = UserDb.getOldUser(mUser)
            user!!.isNewUser = false
            user!!.isAuthenticated = true
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
        }


    }
    suspend fun initializeUser() {
        val mUser = Firebase.auth.currentUser

            if (mUser != null) {
                Log.d(TAG, "initializeUser: user not null initializing")
                /*
                if get failed both in cache and server then retrieve from Local Files
                 */
                user = UserDb.getOldUser(mUser)
                user!!.isAuthenticated = true
                user!!.isNewUser = false
                Log.d(TAG, "initializeUser: initialized")
            } else {
                Log.d(TAG, "initializeUser: user is null")
                user = null
            }
            withContext(Dispatchers.Main){
                userMLD.value = user
            }
    }
    fun reinitializeUser(){
        viewModelScope.launch {
            initializeUser()
        }
    }
    fun signOut(){
        user=null
        userMLD.value=user
    }

    companion object{
        private const val TAG = "ViewModelUserInfo"
        private const val ACCESS_GOOGLE = "GOOGLE"
        private const val ACCESS_PHONE = "PHONE"
    }


}