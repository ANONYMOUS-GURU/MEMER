package com.example.memer.FIRESTORE

import android.util.Log
import com.example.memer.MODELS.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


object FirebaseProfileService {

    private const val TAG = "FirebaseProfileService"

    fun userCoroutine(
        userId: String,
        nameOfUser: String,
        username: String,
        signInType: String,
        phoneNumber: String?
    ) {
        CoroutineScope(IO).launch {
            setProfileData(userId, nameOfUser, username, signInType, phoneNumber)
        }
    }

    private suspend fun checkFirstTimeUser(userId: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference = db.collection("USERS").document(userId)
        var ret: Boolean = false
        Log.d(TAG, "checkFirstTimeUser: Started Checking")
        docIdRef.get().addOnCompleteListener { task ->
            ret = if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    Log.d(TAG, "Document exists!")
                    false
                } else {
                    Log.d(TAG, "Document does not exist!")
                    true
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
                false
            }
        }.await()

        Log.d(TAG, "checkFirstTimeUser: Got Value")

        return ret
    }


    private suspend fun setProfileData(
        userId: String,
        nameOfUser: String,
        username: String,
        signInType: String,
        phoneNumber: String?
    ) {

        val firstTime = checkFirstTimeUser(userId)
        Log.d(TAG, "setProfileData: $firstTime")


        if (firstTime) {
            val db = FirebaseFirestore.getInstance()
            val user = User(
                userId,
                username,
                nameOfUser,
                signInType,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )

            Log.d(TAG, "setProfileData: Here")
            db.collection("USERS").document(userId).set(user).addOnSuccessListener {
                Log.d(TAG, "setProfileData: Success")
            }.addOnFailureListener {
                Log.e(TAG, "setProfileData: Failure", it)
            }
        }
    }
}