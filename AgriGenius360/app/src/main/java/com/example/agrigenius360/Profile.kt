package com.example.agrigenius360


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*


@Composable
fun ProfileScreen(navController: NavHostController) {

    val userName =  CurrentUserSession.user?.username ?: "Guest User"
    val phoneNumber = CurrentUserSession.user?.phoneNumber ?: "N/A"
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.man),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
                    .clip(CircleShape)
            )
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = phoneNumber,
                fontSize = 18.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(50.dp))
            ProfileButton("Log out", {
                signOut(navController, context)
            })

        }
    }
}

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F38)),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 10.dp)
    ) {
        Text(text, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun signOut(navController: NavHostController, context: Context) {
    try {
        Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show()

        navController.navigate("signin") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error signing out: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}