package com.example.agrigenius360

object CurrentUserSession {
    var user: UsersEntity? = null

    fun clearSession() {
        user = null
    }
    val isLoggedIn: Boolean
        get() = user != null
}