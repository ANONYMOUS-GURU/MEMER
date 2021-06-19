package com.example.memer.FIRESTORE

import android.util.Log
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_POST_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_PUBLIC_COLLECTION
import com.example.memer.HELPERS.GLOBAL_INFORMATION.USER_RELATION_COLLECTION
import com.example.memer.MODELS.*
import com.example.memer.MODELS.UserData.Companion.toUserData
import com.example.memer.MODELS.UserProfileInfo.Companion.toUserProfileInfo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object UserDb {

    private const val TAG = "UserDb"

    // TODO(ALL ACCESS TO YOUR OWN USER TABLE EXCEPT DELETE A RECORD APART THAT FROM CLOUD FUNCTION)
    fun updateImageReference(
        imageReference: Pair<String?, String?>,
        userId: String
    ) {
        val data = mapOf(
            "userProfilePicReference" to imageReference.first,
            "userAvatarReference" to imageReference.second
        )

        val dataPublic = mapOf("userAvatarReference" to imageReference.second)

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        batch.set(db.collection(USER_COLLECTION).document(userId), data, SetOptions.merge())
        batch.set(
            db.collection(USER_PUBLIC_COLLECTION).document(userId),
            dataPublic,
            SetOptions.merge()
        )

        batch.commit()

        //TODO(ADD CLOUD FUNCTION FOR UPDATING IN ALL POSTS AND LIKES AND COMMENTS OF USER)
    }

    // TODO(ALL ACCESS)
    fun updateUser(user: UserData) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        batch.set(db.collection(USER_COLLECTION).document(user.userId), user, SetOptions.merge())
        batch.set(
            db.collection(USER_PUBLIC_COLLECTION).document(user.userId),
            user.toUserProfileInfo(),
            SetOptions.merge()
        )
        batch.commit()
        //TODO(ADD CLOUD FUNCTION FOR UPDATING IN ALL POSTS AND LIKES AND COMMENTS OF USER)
    }

    // TODO(SHOULD BE DONE FROM A CLOUD FUNCTION)
    suspend fun addNewUser(userData: UserData) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        batch.set(db.collection(USER_COLLECTION).document(userData.userId), userData)
        batch.set(
            db.collection(USER_PUBLIC_COLLECTION).document(userData.userId),
            userData.toUserProfileInfo()
        )
        batch.commit().await()
    }

    // TODO(CHECK IF USER ID OF REQUEST MATCHES THE USER ID WHERE REQUEST CAME FROM)  <<--- DO THIS FOR ALL REQUESTS
    suspend fun getOldUser(mUser: FirebaseUser): UserData {
        val db = FirebaseFirestore.getInstance()
        return db.collection(USER_COLLECTION).document(mUser.uid).get().await().toUserData() !!
    }

    // TODO(ONLY NEEDS AUTH CHECK)
    suspend fun getRandomUser(userId: String): UserProfileInfo {
        val db = FirebaseFirestore.getInstance()
        return db.collection(USER_PUBLIC_COLLECTION).document(userId).get().await()
            .toUserProfileInfo()
    }

    suspend fun isFollowing(
        currUser: String,
        randomUser: String
    ): Boolean {
        val db = FirebaseFirestore.getInstance()
        val doc =
            db.collection(USER_COLLECTION).document(currUser).collection(USER_RELATION_COLLECTION)
                .document(randomUser)

        var retVal = false
        doc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document: DocumentSnapshot? = task.result
                if (document!!.exists())
                    retVal = document.getBoolean("isFollowing")!!
            } else {
                Log.d(TAG, "isFollowing: Task Failed")
            }
        }.await()

        return retVal
    }



    fun unFollow(
        userId: String,
        randomUserId: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        batch.set(
            db.collection(USER_COLLECTION).document(userId).collection(USER_RELATION_COLLECTION)
                .document(randomUserId), hashMapOf("isFollowing" to false),
            SetOptions.merge()
        )

        // TODO(Add to USER_FOLLOWER_COLLECTION here)

        // TODO(make changes to random users relation collection using cloud functions)

        batch.commit()
    }
    fun follow(
        userId: String,
        randomUserId: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        // TODO(send Notification to randomUserId from userId)
    }


    fun updateUserFollowerList(idOfUser: String, type: String = "ADD") {
        val db = FirebaseFirestore.getInstance()
    }
    fun updateUserFollowingList(idOfUser: String, type: String = "ADD") {
        val db = FirebaseFirestore.getInstance()
    }
    fun updateBookMark(postId: String, type: String = "ADD") {
    }
    fun updateReport(postId: String, type: String = "ADD") {
    }

}