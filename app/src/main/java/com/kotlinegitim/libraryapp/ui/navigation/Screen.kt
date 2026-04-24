package com.kotlinegitim.libraryapp.ui.navigation

// sayfa routerların tanımı.
sealed class Screen(val route : String)
{
    object Login : Screen("login")
    object Register : Screen("register")
}