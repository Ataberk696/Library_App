package com.kotlinegitim.libraryapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kotlinegitim.libraryapp.ui.viewmodel.AuthViewModel
import com.kotlinegitim.libraryapp.ui.viewmodel.SessionState

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onAuthenticated: (String) -> Unit,
    onUnauthenticated: () -> Unit
) {
    // hesaplama sonucunu okuyor
    val sessionState by authViewModel.sessionState.collectAsState()

    LaunchedEffect(sessionState) {
        when (val s = sessionState)
        {
            is SessionState.Authenticated -> onAuthenticated(s.role)
            SessionState.Unauthenticated -> onUnauthenticated()
            SessionState.Initializing -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}