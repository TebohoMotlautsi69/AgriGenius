package com.example.agrigenius360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SoilClassifierScreen() {
    var sand by remember { mutableStateOf("") }
    var silt by remember { mutableStateOf("") }
    var clay by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var crops by remember { mutableStateOf(listOf<String>()) }

    fun classifySoilTexture(sand: Float, silt: Float, clay: Float): String {
        return when {
            sand > 70 && silt < 15 && clay < 15 -> "Loamy Sand"
            sand > 70 && silt < 20 && clay < 10 -> "Sandy"
            sand in 60.0..70.0 && silt in 10.0..20.0 -> "Sandy Loam"
            clay > 20 && silt > 40 -> "Clayey / Silty"
            else -> "Unknown"
        }
    }

    fun recommendCrops(type: String): List<String> {
        return when (type) {
            "Sandy" -> listOf("Carrots", "Peanuts", "Onions")
            "Sandy Loam" -> listOf("Maize", "Tomatoes", "Sunflowers")
            "Loamy Sand" -> listOf("Beans", "Lettuce", "Spinach")
            "Clayey / Silty" -> listOf("Rice", "Sugarcane", "Soybeans")
            else -> emptyList()
        }
    }

    fun analyze() {
        val sandVal = sand.toFloatOrNull()
        val siltVal = silt.toFloatOrNull()
        val clayVal = clay.toFloatOrNull()

        if (sandVal != null && siltVal != null && clayVal != null) {
            soilType = classifySoilTexture(sandVal, siltVal, clayVal)
            crops = recommendCrops(soilType)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FEF5))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🧪 Sand Quality & Crop Advisor", fontSize = 20.sp, modifier = Modifier.padding(20.dp))

        OutlinedTextField(
            value = sand,
            onValueChange = { sand = it },
            label = { Text("Sand (%)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        OutlinedTextField(
            value = silt,
            onValueChange = { silt = it },
            label = { Text("Silt (%)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        OutlinedTextField(
            value = clay,
            onValueChange = { clay = it },
            label = { Text("Clay (%)") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Button(
            onClick = { analyze() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F38)),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text("Analyze", color = Color.White)
        }

        if (soilType.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFF087F38))
                    .padding(16.dp)
            ) {
                Text("🧱 Soil Type: $soilType", color = Color.White, fontSize = 18.sp)
                Text("🌱 Recommended Crops:", color = Color.White, fontSize = 18.sp)
                crops.forEach { crop ->
                    Text("• $crop", color = Color.White)
                }
            }
        }
    }
}
