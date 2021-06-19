package com.example.memer.HELPERS

import android.content.Context
import com.example.memer.MODELS.UserData
import java.io.File
import java.io.FileOutputStream

object InternalStorage {

    private const val userFolderName = "User"

    private fun writeUserString(fileName: String, fileContents: String, context: Context){
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val textFile = File(folder, fileName)
        context.openFileOutput(textFile.absolutePath,Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    private fun readUserString(fileName: String, context: Context):String{
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val textFile = File(folder, fileName)
        return textFile.readText(Charsets.UTF_8)
    }
    private fun readUserLong(fileName: String, context: Context):Long{
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val textFile = File(folder, fileName)
        return textFile.readText(Charsets.UTF_8).toLong()
    }

    fun deleteUser(context: Context){
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val ret = folder.deleteRecursively()
        if(!ret){
            throw Throwable("Error While Deleting User Files")
        }
    }
    fun readUser(context: Context) : UserData{
        return UserData(userId = readUserString("userId",context),
            username = readUserString("username",context),

            userAvatarReference = if (readUserString("userAvatarReference", context)
                    .isEmpty()) null else readUserString("userAvatarReference",context),
            userProfilePicReference = if(readUserString("userProfilePicReference",context)
                    .isEmpty()) null else readUserString("userProfilePicReference",context),

            nameOfUser = readUserString("nameOfUser",context),
            bio = readUserString("bio",context),
            signInType = readUserString("signInType",context),
            phoneNumber = readUserString("phoneNumber",context),
            userFollowersCount = readUserLong("userFollowersCount",context),
            userFollowingCount = readUserLong("userFollowersCount",context),
            userPostCount = readUserLong("userPostCount",context))
    }
    fun writeUser(mUser: UserData,context: Context){
        writeUserString("userId",mUser.userId,context)
        writeUserString("username",mUser.username,context)
        writeUserString("userAvatarReference",mUser.userAvatarReference ?:  "",context)
        writeUserString("userProfilePicReference",mUser.userProfilePicReference ?: "",context)
        writeUserString("nameOfUser",mUser.nameOfUser,context)
        writeUserString("bio",mUser.bio,context)
        writeUserString("signInType",mUser.signInType,context)
        writeUserString("phoneNumber",mUser.phoneNumber ?: "",context)
        writeUserString("userFollowersCount",mUser.userFollowersCount.toString(),context)
        writeUserString("userFollowersCount",mUser.userFollowersCount.toString(),context)
        writeUserString("userPostCount",mUser.userPostCount.toString(),context)
    }

    fun updateUserImageReference(userProfilePicReference:String?,userAvatarReference:String?,context: Context){
        writeUserString("userProfilePicReference",userProfilePicReference ?: "",context)
        writeUserString("userAvatarReference",userAvatarReference ?:  "",context)
    }

}