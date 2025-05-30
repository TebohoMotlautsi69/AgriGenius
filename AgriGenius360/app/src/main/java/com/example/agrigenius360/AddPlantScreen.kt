package com.example.agrigenius360

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    plantDAO: PlantDAO,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var plantName by remember { mutableStateOf("") }
    var plantType by remember { mutableStateOf("") }
    var frequencyMeasure by remember { mutableStateOf("") }
    var optimalRate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text (text = "Add New Plant") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter Plant Details",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = plantName,
                onValueChange = { plantName = it },
                label = { Text("Plant Name (e.g., 'Tomato Plant A')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plantType,
                onValueChange = { plantType = it },
                label = { Text("Plant Type (e.g., 'Tomato', 'Basil')") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = optimalRate,
                onValueChange = { newValue ->
                    if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        optimalRate = newValue
                    }
                },
                label = { Text("Optimal Daily Growth Rate (cm/day)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = frequencyMeasure,
                onValueChange = { frequencyMeasure = it },
                label = { Text("Measurement Frequency (days, optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val rate = optimalRate.toFloatOrNull()
                    val frequencyMeasure = frequencyMeasure.toInt()
                    if (plantName.isBlank() || plantType.isBlank() || rate == null || rate <= 0) {
                        errorMessage = "Please fill all fields and provide a valid optimal rate."
                    } else {
                        scope.launch {
                            try {
                                val newPlantId = plantDAO.insertPlant(
                                    PlantEntity(
                                        name = plantName,
                                        type = plantType,
                                        optimalGrowthRateCmPerDay = rate,
                                        plantDate = System.currentTimeMillis(),
                                        measurementFrequencyDays = frequencyMeasure
                                    )
                                ).toInt()

                                Toast.makeText(context, "Plant added successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error adding plant: ${e.localizedMessage}"
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Plant")
            }
        }
    }
}