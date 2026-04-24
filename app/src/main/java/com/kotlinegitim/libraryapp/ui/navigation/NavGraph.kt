package com.kotlinegitim.libraryapp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlinegitim.libraryapp.ui.screen.LoginScreen
import com.kotlinegitim.libraryapp.ui.screen.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()){
    NavHost(navController = navController, startDestination = Screen.Login.route)
    {
        composable(Screen.Login.route)
        { LoginScreen(
            onNavigateToRegister = {
                navController.navigate(Screen.Register.route)
            }
        ) }

        composable(Screen.Register.route)
        { RegisterScreen(
            onRegisterSuccess = {
                navController.navigate(Screen.Login.route)
            }
        ) }
    }
}