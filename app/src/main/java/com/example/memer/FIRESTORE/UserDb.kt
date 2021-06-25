package com.example.memer.FIRESTORE

import android.util.Log

import com.example.memer.MODELS.*
import com.example.memer.MODELS.UserData.Companion.toUserData
import com.example.memer.MODELS.UserEditInfo.Companion.toUserEditInfo
import com.example.memer.MODELS.UserProfileInfo.Companion.toUserProfileInfo
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object UserDb {

    private const val TAG = "UserDb"

    fun updateImageReference(
        imageReference: Pair<String?, String?>,
        userId: String
    ) {
        val data = mapOf(
            UserElement.UserProfilePicReference.value to imageReference.first,
            UserElement.UserAvatarReference.value to imageReference.second
        )

        val dataPublic = mapOf(UserElement.UserAvatarReference to imageReference.second)

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        batch.set(db.collection(FireStoreCollection.User.value).document(userId), data, SetOptions.merge())
        batch.set(
            db.collection(FireStoreCollection.UserPublic.value).document(userId),
            dataPublic,
            SetOptions.merge()
        )

        batch.commit()

        //            .addOnSuccessListener {
//                val docsLikes = db.collection(LIKES_COLLECTION).whereEqualTo(userIdElement,user.userId)
//                val docsComments = db.collection(COMMENTS_COLLECTION).whereEqualTo(userIdElement,user.userId)
//                val docsPost = db.collection(POST_COLLECTION).whereEqualTo("postOwnerId",user.userId)
//                // TODO(SAME FOR BOOKMARKS AND REPORTS)
//                //TODO(ADD CLOUD FUNCTION FOR UPDATING IN ALL POSTS AND LIKES AND COMMENTS OF USER)
//            }
    }

    fun updateUser(user: UserData) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        batch.set(db.collection(FireStoreCollection.User.value).document(user.userId), user, SetOptions.merge())
        batch.set(
            db.collection(FireStoreCollection.UserPublic.value).document(user.userId),
            user.toUserProfileInfo(),
            SetOptions.merge()
        )
        batch.commit()

//            .addOnSuccessListener {
//                val docsLikes = db.collection(LIKES_COLLECTION).whereEqualTo(userIdElement,user.userId)
//                val docsComments = db.collection(COMMENTS_COLLECTION).whereEqualTo(userIdElement,user.userId)
//                val docsPost = db.collection(POST_COLLECTION).whereEqualTo("postOwnerId",user.userId)
//                // TODO(SAME FOR BOOKMARKS AND REPORTS)
//                //TODO(ADD CLOUD FUNCTION FOR UPDATING IN ALL POSTS AND LIKES AND COMMENTS OF USER)
//            }

    }

    // TODO(SHOULD BE DONE FROM A CLOUD FUNCTION)
    suspend fun addNewUser(userData: UserData) {
        val db = FirebaseFirestore.getInstance()
        val docIdRef = FirebaseFirestore.getInstance().collection(FireStoreCollection.User.value).document(userData.userId)
        val doc = docIdRef.get().await()
        val batch = db.batch()

        if(!doc.exists()){
            Log.d(TAG, "addNewUser: Doc Does Not exist .. Writing New User .. userId - ${userData.userId}")
            batch.set(db.collection(FireStoreCollection.User.value).document(userData.userId), userData)
            batch.set(
                db.collection(FireStoreCollection.UserPublic.value).document(userData.userId),
                userData.toUserProfileInfo()
            )
        }
        else{
            Log.d(TAG, "addNewUser: Doc exists overwriting profile  userId - ${userData.userId}")
            batch.set(db.collection(FireStoreCollection.User.value).document(userData.userId), userData.toUserEditInfo(),
                SetOptions.merge())
            batch.set(
                db.collection(FireStoreCollection.UserPublic.value).document(userData.userId),
                userData.toUserEditInfo(),
                SetOptions.merge()
            )
        }
        batch.commit().await()
    }


    // TODO(CHECK IF USER ID OF REQUEST MATCHES THE USER ID WHERE REQUEST CAME FROM)  <<--- DO THIS FOR ALL REQUESTS
    suspend fun getOldUser(mUser: FirebaseUser): UserData {
        val db = FirebaseFirestore.getInstance()
        return db.collection(FireStoreCollection.User.value).document(mUser.uid).get().await().toUserData() !!
    }
    suspend fun getRandomUser(userId: String): UserProfileInfo {
        val db = FirebaseFirestore.getInstance()
        val ret = db.collection(FireStoreCollection.User.value).document(userId).get().await()
            .toUserProfileInfo()
        Log.d(TAG, "getRandomUser: ${ret.postCount}")
        return ret
    }


    fun updateBookMark(postId: String, type: String = "ADD") {
    }
    fun updateReport(postId: String, type: String = "ADD") {
    }

}