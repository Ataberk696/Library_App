package com.kotlinegitim.libraryapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlinegitim.libraryapp.ui.viewmodel.AuthState
import com.kotlinegitim.libraryapp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit){
    val authViewModel : AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success){
            onRegisterSuccess()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Kütüphane Sistemi Kayıt")
        Spacer(modifier = Modifier.height(10.dp))
        Text("Kayıt Ol")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading,
            value = email,
            label = {Text("E-posta")},
            onValueChange = { value -> email = value},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading,
            value = password,
            onValueChange = {password = it},
            label = {Text("Şifre")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (authState is AuthState.Loading){
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            }
        } else{
            Button(onClick = {
                authViewModel.signUp(email,password)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Kayıt Ol")
            }
        }

        if (authState is AuthState.Success){
            Text("Kayıt Başarılı")
        } else if (authState is AuthState.Error){
            Text((authState as AuthState.Error).message)
        }

    }
}