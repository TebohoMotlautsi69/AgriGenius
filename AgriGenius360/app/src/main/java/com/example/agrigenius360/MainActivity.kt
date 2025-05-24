package com.example.agrigenius360

import OtpVerificationScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
    val application = LocalContext.current.applicationContext as AgriGeniusApplication
    val usersDAO = application.usersDAO
    val plantGrowthDAO = application.plantGrowthDAO

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
            composable("signin")      { SignInScreen(usersDAO = usersDAO, navController) }
            composable("signup")      { SignUpScreen(usersDAO = usersDAO, navController) }
            composable("otpverify/{phoneNumber}/{otp}")   { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?:""
                val otp = backStackEntry.arguments?.getString("otp") ?:""
                OtpVerificationScreen(usersDAO = usersDAO, navController, phoneNumber, otp ) }
            composable("home")         { HomeScreen(navController) }
//            composable("notifications"){ NotificationsScreen() }
            composable("profile")      { ProfileScreen() }
            composable("growth")       { PlantGrowthCalculatorScreen(plantGrowthDAO = plantGrowthDAO, navController = navController) }
            composable("sand")         { SoilClassifierScreen() }
            composable("growthHistory") { PlantGrowthHistoryScreen(plantGrowthDAO = plantGrowthDAO) }
        }
    }
}

@Composable
public fun currentRoute(nav: NavHostController): String? =
    nav.currentBackStackEntryAsState().value?.destination?.route
