package com.example.agrigenius360

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, plantDAO: PlantDAO) {
    val plants by plantDAO.getAllPlants().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    ) {
                        Text(
                            text = "Hi Username User,",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "How can I help\nyou today?",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addPlant") }) {
                Icon(Icons.Filled.Add, "Add new plant")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FeatureTile(
                        drawableId = R.drawable.growth,
                        onClick    = { /* Consider action for this tile now that plants are listed */ }
                    )

                    FeatureTile(
                        drawableId = R.drawable.moisture,
                        onClick    = { navController.navigate("sand") }
                    )

                    FeatureTile(
                        drawableId = R.drawable.barchart,
                        onClick    = { /* Placeholder */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Plants",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (plants.isEmpty()) {
                    Text(
                        "No plants added yet. Click '+' to add one!",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(plants) { plant ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clickable { navController.navigate("plantHistory/${plant.id}") },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(plant.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                    Text(plant.type, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Optimal Rate: %.2f cm/day".format(plant.optimalGrowthRateCmPerDay), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun FeatureTile(drawableId: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFD9D9D9),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
        }
    }
}