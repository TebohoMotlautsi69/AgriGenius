package com.example.agrigenius360

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun SignUpScreen(usersDAO: UsersDAO, navController: NavHostController) {

    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.applogo2), contentDescription = "App Logo", modifier =  Modifier.size(200.dp))
            Text(text = "AgriGenius360", fontSize = 32.sp, fontWeight = FontWeight.Bold )
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Enter username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Enter phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if(userName.isNotBlank() && phoneNumber.isNotBlank()){
                        CoroutineScope(Dispatchers.IO).launch {
                            try{
                                usersDAO.insert(
                                    UsersEntity(
                                        username = userName,
                                        phoneNumber = phoneNumber
                                    )
                                )
                                withContext(Dispatchers.Main){
                                    navController.navigate("Signin")
                                }
                            }catch (e: Exception){
                                withContext(Dispatchers.Main){
                                    showError = true
                                    errorMessage = "Phone number already in use"
                                }
                            }
                        }
                    }else{
                        showError = true
                        errorMessage = "Please ensure username and phonenumber are provided"
                    }
                    navController.navigate("signin")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
            if(showError){
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
