package com.example.agrigenius360

import OtpVerificationScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgroApp()
        }
    }
}

@Composable
fun AgroApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val route = currentRoute(navController)
            if (route in listOf("home", "notifications", "profile")) {
                BottomBarNav(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "signin",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("signin")      { SignInScreen(navController) }
            composable("signup")      { SignUpScreen(navController) }
            composable("otpverify")   { OtpVerificationScreen(navController) }
            composable("home")         { HomeScreen(navController) }
//            composable("notifications"){ NotificationsScreen() }
            composable("profile")      { ProfileScreen() }
            composable("growth")       { PlantGrowthCalculatorScreen() }
            composable("sand")         { SoilClassifierScreen() }
        }
    }
}

@Composable
public fun currentRoute(nav: NavHostController): String? =
    nav.currentBackStackEntryAsState().value?.destination?.route
