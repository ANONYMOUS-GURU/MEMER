package com.example.memer.HELPERS

import android.content.Context
import android.util.Log
import com.example.memer.MODELS.UserData
import com.example.memer.MODELS.UserTextEditInfo
import java.io.File
import java.io.FileOutputStream

object InternalStorage {

    private const val userFolderName = "User"

    private fun writeUserString(fileName: String, fileContents: String, context: Context){
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val textFile = File(folder, fileName)
        textFile.writeText(fileContents,Charsets.UTF_8)
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
        for(filename in arrayOf("userId","username","userAvatarReference","userProfilePicReference",
            "nameOfUser","bio","signInType","phoneNumber","userFollowersCount","userFollowingCount",
            "userPostCount")){
            val textFile = File(folder, filename)
            if(textFile.exists())
                textFile.delete()
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
            userFollowingCount = readUserLong("userFollowingCount",context),
            userPostCount = readUserLong("userPostCount",context))
    }
    fun writeUser(mUser: UserData,context: Context){

        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        for(filename in arrayOf("userId","username","userAvatarReference","userProfilePicReference",
            "nameOfUser","bio","signInType","phoneNumber","userFollowersCount","userFollowingCount",
            "userPostCount")){
            val textFile = File(folder, filename)
            if(textFile.exists())
                textFile.delete()
        }

        writeUserString("userId",mUser.userId,context)
        writeUserString("username",mUser.username,context)
        writeUserString("userAvatarReference",mUser.userAvatarReference ?:  "",context)
        writeUserString("userProfilePicReference",mUser.userProfilePicReference ?: "",context)
        writeUserString("nameOfUser",mUser.nameOfUser,context)
        writeUserString("bio",mUser.bio,context)
        writeUserString("signInType",mUser.signInType,context)
        writeUserString("phoneNumber",mUser.phoneNumber ?: "",context)
        writeUserString("userFollowersCount",mUser.userFollowersCount.toString(),context)
        writeUserString("userFollowingCount",mUser.userFollowersCount.toString(),context)
        writeUserString("userPostCount",mUser.userPostCount.toString(),context)
    }
    fun userExists(context: Context):Boolean{
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        val textFile = File(folder, "username")
        return textFile.exists()
    }

    fun updateUserImageReference(userProfilePicReference:String?,userAvatarReference:String?,context: Context){
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        for(filename in arrayOf("userProfilePicReference","userAvatarReference")){
            val textFile = File(folder, filename)
            if(textFile.exists())
                textFile.delete()
        }

        writeUserString("userProfilePicReference",userProfilePicReference ?: "",context)
        writeUserString("userAvatarReference",userAvatarReference ?:  "",context)
    }
    fun writeUserTextEditInfo(mUser: UserTextEditInfo,context: Context){
        val folder = context.getDir(userFolderName, Context.MODE_PRIVATE)
        for(filename in arrayOf("username","nameOfUser","bio")){
            val textFile = File(folder, filename)
            if(textFile.exists())
                textFile.delete()
        }

        writeUserString("username",mUser.username,context)
        writeUserString("nameOfUser",mUser.nameOfUser,context)
        writeUserString("bio",mUser.bio,context)
    }

    private const val TAG = "InternalStorage"
}