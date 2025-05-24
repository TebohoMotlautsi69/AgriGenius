package com.example.agrigenius360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

@Composable
fun PlantGrowthHistoryScreen(plantGrowthDAO: PlantGrowthDAO) {
    val allRecords by plantGrowthDAO.getAllRecords().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FEF5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌱 Growth History", fontSize = 28.sp, modifier = Modifier.padding(bottom = 24.dp))

        if (allRecords.isEmpty()) {
            Text("No growth records yet. Go back and add some!", fontSize = 18.sp, color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(allRecords) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Plant: ${record.plantName} (${record.cropType})", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
                            Text("Initial Height: ${record.initialHeight} cm", fontSize = 16.sp)
                            Text("Final Height: ${record.finalHeight} cm", fontSize = 16.sp)
                            Text("Days: ${record.days}", fontSize = 16.sp)
                            Text("Growth Rate: %.2f cm/day".format(record.growthRate), fontSize = 16.sp, color = Color(0xFF087F38))
                            Text("Recorded: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(record.timestamp))}", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}