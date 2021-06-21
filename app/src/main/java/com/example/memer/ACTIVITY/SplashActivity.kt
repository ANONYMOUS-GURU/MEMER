package com.example.memer.ACTIVITY

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import com.example.memer.FRAGMENTS.FragmentHomePage
import com.example.memer.HELPERS.InternalStorage
import com.example.memer.MODELS.UserData
import com.example.memer.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private val mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Launched Splash Screen")


        val firstTimeOpen = false
        if(firstTimeOpen){
            doFirstTimeWork()
            val intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)

        }
        else{
            val user = doUserCheck()

            if(user == null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("User",user)
                startActivity(intent)
            }
            doStartUpWork()
        }

        finish()
    }

    private fun doUserCheck() : UserData? {
        if (mAuth.currentUser == null) {
            Log.d(TAG, "doUserCheck: No User Found")
            return null
        }
        return when {
            InternalStorage.userExists(application) -> {
                Log.d(TAG, "doUserCheck: User Found Fetching Data")
                InternalStorage.readUser(application)
            }
            else -> {
                Log.d(TAG, "doUserCheck: User Not Found in Internal Going back to Login")
                null
            }
        }
    }

    private fun doStartUpWork() {
    }

    private fun doFirstTimeWork() {
    }

    companion object{
        private const val TAG = "SplashActivity"
    }

}