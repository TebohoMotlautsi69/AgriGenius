package com.example.agrigenius360

import OtpVerificationScreen
import android.health.connect.datatypes.ExerciseRoute
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agrigenius360.ui.theme.AgriGenius360Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriGenius360Theme {
                AgroApp(startRoute = intent.getStringExtra("route"))
            }
        }
    }
}

@Composable
fun AgroApp(startRoute: String?) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as AgriGeniusApplication
    val usersDAO = application.usersDAO
    val plantGrowthDAO = application.plantGrowthDAO
    val plantDAO = application.plantDAO

    LaunchedEffect(startRoute) {
        if (startRoute != null){
            navController.navigate(startRoute){
                popUpTo("home"){
                    inclusive = true
                }
            }
        }
    }

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
            composable("signin") { SignInScreen(usersDAO = usersDAO, navController = navController) }
            composable("signup") { SignUpScreen(usersDAO = usersDAO, navController = navController) }
            composable("otpverify/{phoneNumber}/{otp}") { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
                val otp = backStackEntry.arguments?.getString("otp") ?: ""
                OtpVerificationScreen(usersDAO = usersDAO, navController = navController, phoneNumber = phoneNumber, otp = otp)
            }
            composable("home") { HomeScreen(navController = navController, plantDAO = plantDAO) }
            composable("notifications") { Notifications(navController = navController) }
            composable("profile") { ProfileScreen(navController = navController) }
            composable("growth/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
                PlantGrowthCalculatorScreen(plantGrowthDAO = plantGrowthDAO, plantDAO = plantDAO, navController = navController, plantId = plantId)
            }
            composable("sand") { SoilClassifierScreen() }
            composable("growthHistory/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
                PlantGrowthHistoryScreen(plantGrowthDAO = plantGrowthDAO, plantDAO = plantDAO, navController = navController, plantId = plantId)
            }
            composable("addPlant") {
                AddPlantScreen(plantDAO = plantDAO, navController = navController)
            }
            composable("addMeasurement/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: 0
                AddMeasurementScreen(
                    plantGrowthDAO = plantGrowthDAO,
                    navController = navController,
                    plantId = plantId
                )
            }
        }
    }
}

@Composable
fun currentRoute(nav: NavHostController): String? =
    nav.currentBackStackEntryAsState().value?.destination?.route