package com.example.memer.VIEWMODELS

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.memer.FIRESTORE.UserDb
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_COLLECTION
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.MODELS.LoginState
import com.example.memer.MODELS.UserData
import com.example.memer.MODELS.UserTextEditInfo
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.math.log
import kotlin.math.sign


class ViewModelLogin(application: Application) : AndroidViewModel(application) {

    var userId:String? = Firebase.auth.currentUser?.uid

    var userTextEditInfo: UserTextEditInfo = UserTextEditInfo("", "", "")

    private var imageProfileRef:String? = null
    private var signInType:String = ""

    private var imageRef: String? = null
    private val imageRefMLD= MutableLiveData<String?>()
    val imageRefLD:LiveData<String?>
        get() = imageRefMLD

    private var loginState: LoginState
    private var loginStateMLD: MutableLiveData<LoginState> = MutableLiveData<LoginState>()
    val loginStateLD : LiveData<LoginState>
        get() = loginStateMLD

    init {
        imageRefMLD.value = imageRef
        loginState = if(userId == null)
            LoginState.LoggedOut
        else
            LoginState.UserAvailable("")

        loginStateMLD.value = loginState
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        signInType = ACCESS_PHONE

        loginState = LoginState.TryingLogIn(signInType)
        loginStateMLD.value = loginState

        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth.currentUser
                userId  = user!!.uid
                val docIdRef = FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(userId !!)
                docIdRef.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document!!.exists()) {
                            Log.d(TAG, "signInWithPhoneAuthCredential: Old User Found")
                            writeOldUser(user, signInType)

                        } else {
                            Log.d(TAG, "signInWithPhoneAuthCredential: New User Found")
                            updateLoginState(user, signInType)
                        }
                    } else {
                        userId = null
                        Log.e(TAG, "signInWithPhoneAuthCredential: Exception while checking if document exists",it.exception)
                        userId = null
                        loginState = LoginState.LogInFailed("", it.exception !!, signInType)
                        loginStateMLD.value = loginState
                    }
                }
            } else {
                Log.e(TAG, "signInWithPhoneAuthCredential: sign in failed",task.exception)
                userId = null
                loginState = LoginState.LogInFailed("", task.exception!!, signInType)
                loginStateMLD.value = loginState
            }
        }
    }
    fun firebaseAuthWithGoogle(idToken: String) {
        signInType = ACCESS_GOOGLE
        loginState = LoginState.TryingLogIn(signInType)
        loginStateMLD.value = loginState
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val mAuth = Firebase.auth
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    userId  = user!!.uid
                    val docIdRef = FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(userId !!)
                    docIdRef.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val document = it.result
                            if (document!!.exists()) {
                                Log.d(TAG, "firebaseAuthWithGoogle: Old User Found")
                                writeOldUser(user, signInType)

                            } else {
                                Log.d(TAG, "firebaseAuthWithGoogle: New User Found")
                                updateLoginState(user, signInType)
                            }
                        } else {
                            userId = null
                            Log.e(TAG, "firebaseAuthWithGoogle: Exception while checking if document exists",it.exception)
                            userId = null
                            loginState = LoginState.LogInFailed("", it.exception !!, signInType)
                            loginStateMLD.value = loginState
                        }
                    }
                } else {
                    Log.e(TAG, "firebaseAuthWithGoogle: sign in failed",task.exception)
                    userId = null
                    loginState = LoginState.LogInFailed("", task.exception!!, signInType)
                    loginStateMLD.value = loginState
                }
            }
    }

    private fun updateLoginState(user: FirebaseUser, signInType: String){
        userId = Firebase.auth.currentUser!!.uid
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                loginState = LoginState.LogInSuccess("", signInType, true)
                loginStateMLD.value = loginState
            }
        }
    }

    private fun writeOldUser(user: FirebaseUser, signInType: String){
        userId = Firebase.auth.currentUser!!.uid
        viewModelScope.launch {
            writeUser(user)
            Log.d(TAG, "writeOldUser: Wrote Old User")
            withContext(Dispatchers.Main){
                loginState = LoginState.Completed("", signInType)
                loginStateMLD.value = loginState
            }
        }
    }
    private suspend fun writeUser(user: FirebaseUser){
        val mUser = UserDb.getOldUser(user)
        Log.d(TAG, "writeUser: Writing Old User ${mUser.username}")
        InternalStorage.writeUser(mUser, getApplication())
    }
    fun updateUserImageReference(_user: Pair<String?, String?>){
        viewModelScope.launch {
            InternalStorage.updateUserImageReference(_user.first, _user.second, getApplication())
            withContext(Dispatchers.Main){
                imageProfileRef = _user.first
                imageRef = _user.second
                imageRefMLD.value = imageRef
            }
        }
    }
    fun writeNewUser(userTextEditInfo: UserTextEditInfo){
        viewModelScope.launch {
            InternalStorage.writeUser(
                UserData(
                    userId = userId!!,
                    userPostCount = 0,
                    userFollowingCount = 0,
                    userFollowersCount = 0,
                    userProfilePicReference = imageProfileRef,
                    userAvatarReference = imageRef,
                    username = userTextEditInfo.username,
                    nameOfUser = userTextEditInfo.nameOfUser,
                    bio = userTextEditInfo.bio,
                    signInType = signInType,
                    phoneNumber = if (signInType == ACCESS_PHONE) Firebase.auth.currentUser?.phoneNumber else ""
                ),
                getApplication()
            )
            UserDb.addNewUser(InternalStorage.readUser(getApplication()))
            withContext(Dispatchers.Main){
                loginState = LoginState.Completed("", signInType)
                loginStateMLD.value = loginState
            }
        }
    }


    companion object{
        private const val TAG = "VMLogin"
        private const val ACCESS_GOOGLE = "GOOGLE"
        private const val ACCESS_PHONE = "PHONE"
    }

}