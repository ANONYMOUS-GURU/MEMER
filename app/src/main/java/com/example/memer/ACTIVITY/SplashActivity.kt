package com.example.memer.ACTIVITY

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class SplashActivity : AppCompatActivity() {
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
            doStartUpWork()
             val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        finish()
    }

    private fun doStartUpWork() {
    }

    private fun doFirstTimeWork() {
    }

    companion object{
        private const val TAG = "SplashActivity"
    }

}