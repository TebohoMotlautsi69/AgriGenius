package com.example.agrigenius360

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarNav(navController: NavHostController) {

    val items = listOf(
        NavItem("Home", Icons.Filled.Home, "home"),
        NavItem("Notifications", Icons.Filled.Notifications, "notification"),
        NavItem("Profile", Icons.Filled.Person, "profile")
    )
    val currentRoute = currentRoute(navController)
    NavigationBar {
        items.forEach { item->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                          },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                       },
                label = {
                    Text(item.label)
                        }
            )

        }

    }

}

