package com.example.agrigenius360

import android.widget.Toast
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
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(usersDAO: UsersDAO, navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.applogo2), contentDescription = "App Logo", modifier =  Modifier.size(200.dp))
            Text(text = "AgriGenius360", fontSize = 32.sp, fontWeight = FontWeight.Bold )
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Enter phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if(errorMessage.isNotEmpty()){
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val user = usersDAO.findByPhone(phoneNumber)
                        if(user != null){
                            val otp = (100000..999999).random().toString()
                            val expiry = System.currentTimeMillis() + 2 * 60 * 1000

                            usersDAO.storeOtp(phoneNumber, otp, expiry)
                            Toast.makeText(context, "OTP: $otp", Toast.LENGTH_SHORT).show()

                            navController.navigate("otpverify/${phoneNumber}/${otp}")
                        }else{
                            errorMessage = "Please provide a registered phone number"
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Sign In")
            }

            Spacer(Modifier.height(20.dp))
            Text("or")
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate("signup")
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Sign Up")
            }
        }
    }
}