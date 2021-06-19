package com.example.memer.VIEWMODELS

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.MODELS.LoginState
import com.example.memer.MODELS.UserBasicInfo
import com.example.memer.MODELS.UserData.Companion.toUserData
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ViewModelLogin (application: Application) : AndroidViewModel(application) {

    private lateinit var loginState: LoginState
    private var loginStateMLD: MutableLiveData<LoginState> = MutableLiveData<LoginState>()
    val loginStateLD : LiveData<LoginState>
        get() = loginStateMLD

    init {
        loginState = LoginState.LoggedOut
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        loginState = LoginState.TryingLogIn(ACCESS_PHONE)
        loginStateMLD.value = loginState

        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = task.result?.user
                val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                if(user!=null){
                    if(isNewUser){
                        createAndWriteNewUser(user, ACCESS_PHONE)
                    }

                    else {
                        Log.d(TAG, "firebaseAuthWithGoogle: Start getting user data")
                        writeOldUser(user, ACCESS_PHONE)
                    }
                }
            } else {
                var msg = ""
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    msg="Invalid Verification Code"
                }

                loginState = LoginState.LogInFailed(msg,task.exception!!, ACCESS_PHONE)
                loginStateMLD.value = loginState
            }
        }
    }
    fun firebaseAuthWithGoogle(idToken: String) {
        loginState = LoginState.TryingLogIn(ACCESS_GOOGLE)
        loginStateMLD.value = loginState
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true
                    if(user!=null){
                        if(isNewUser) {
                            createAndWriteNewUser(user, ACCESS_GOOGLE)
                            /*
                            * Make a new user and wait for above function to complete before
                            * proceeding. Also store user data in a local file to make use of it in
                            * ViewModelUser by directly accessing these files. Do them synchronously
                            * */

                        }
                        else {
                            Log.d(TAG, "firebaseAuthWithGoogle: Start getting user data")
                            /*
                            *   Download User Data into a local file named User , so that initialize
                            *   user doesn't need to fetch data from the internet . Also make updates
                            *   on this file as user info changes and finally delete it when logged
                            *   out
                            * */
                            writeOldUser(user, ACCESS_GOOGLE)
                        }
                    }

                } else {
                    loginState = LoginState.LogInFailed("",task.exception!!, ACCESS_GOOGLE)
                    loginStateMLD.value = loginState
                }
            }
    }

    private fun createAndWriteNewUser(user: FirebaseUser, signInType:String){
        val name =  user.displayName !!
        viewModelScope.launch {
            UserDb.addNewUser(UserBasicInfo(userId = user.uid, nameOfUser = (user.uid.subSequence(0, 5))
                .toString(), username = user.uid, signInType = signInType, phoneNumber = null)
                .toUserData())
            writeUser(user)
            withContext(Dispatchers.Main){
                loginState = LoginState.LogInSuccess("",signInType,true)
                loginStateMLD.value = loginState
            }
        }
    }
    private fun writeOldUser(user:FirebaseUser,signInType: String){
        viewModelScope.launch {
            writeUser(user)
            withContext(Dispatchers.Main){
                loginState = LoginState.LogInSuccess("",signInType,true)
                loginStateMLD.value = loginState
            }
        }
    }
    private suspend fun writeUser(user:FirebaseUser){
        val mUser = UserDb.getOldUser(user)
        InternalStorage.writeUser(mUser,getApplication())
    }

    companion object{
        private const val TAG = "VMLogin"
        private const val ACCESS_GOOGLE = "GOOGLE"
        private const val ACCESS_PHONE = "PHONE"
    }
}