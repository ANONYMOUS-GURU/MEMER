package com.example.memer.MODELS

sealed class LoginState{
    data class LogInSuccess(val message:String,val accessType:String,val newUser:Boolean) : LoginState()
    object LoggedOut : LoginState()
    data class LogInFailed(val message: String,val exception:Exception,val accessType: String) :LoginState()
    data class TryingLogIn(val accessType: String):LoginState()
}