package com.example.agrigenius360

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantGrowthHistoryScreen(
    plantGrowthDAO: PlantGrowthDAO,
    plantDAO: PlantDAO,
    navController: NavHostController,
    plantId: Int
) {
    val measurements by plantGrowthDAO.getMeasurementsForPlant(plantId)
        .collectAsState(initial = emptyList())

    val plant by produceState<PlantEntity?>(initialValue = null, plantDAO, plantId) {
        value = plantDAO.getPlantById(plantId)
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }


    val avgDailyGrowth = remember(measurements) {
        if (measurements.size < 2) 0.0
        else {
            val initialMeasurement = measurements.first()
            val latestMeasurement = measurements.last()
            val totalHeightDiff = latestMeasurement.heightCm - initialMeasurement.heightCm
            val totalDays = (latestMeasurement.measurementDate - initialMeasurement.measurementDate) / (1000 * 60 * 60 * 24).toDouble()
            if (totalDays > 0) totalHeightDiff / totalDays else 0.0
        }
    }

    // Determine Growth Status Color and Text
    val growthStatus = remember(avgDailyGrowth, plant?.optimalGrowthRateCmPerDay) {
        val optimalRate = plant?.optimalGrowthRateCmPerDay ?: 0.0
        when {
            optimalRate == 0.0 -> { // No optimal rate defined or zero
                Pair("Optimal rate not set", Color.Gray)
            }
            avgDailyGrowth >= optimalRate * 0.95 -> { // Within 5% of optimal or better
                Pair("Growth: Excellent", Color.Green)
            }
            avgDailyGrowth >= optimalRate * 0.75 -> { // Between 75% and 95% of optimal
                Pair("Growth: Good", Color(0xFFFFA500)) // Orange
            }
            else -> { // Below 75% of optimal
                Pair("Growth: Slow", Color.Red)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(plant?.name ?: "Plant History") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${plant?.name ?: "Plant"} Growth Trends",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Growth Status Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = growthStatus.second.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = growthStatus.first,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = growthStatus.second
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Avg Daily Growth: %.2f cm/day".format(avgDailyGrowth),
                        fontSize = 14.sp
                    )
                    plant?.optimalGrowthRateCmPerDay?.let {
                        Text(
                            text = "Optimal: %.2f cm/day".format(it),
                            fontSize = 14.sp
                        )
                    }
                }
            }


            // Growth Chart
            if (measurements.size > 1) {
                GrowthChart(measurements = measurements)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    text = "Add more measurements to see growth trends and status.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // All Measurements List
            Text(
                text = "All Measurements",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(measurements.reversed()) { measurement ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Height: %.1f cm".format(measurement.heightCm))
                            Text(dateFormatter.format(Date(measurement.measurementDate)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrowthChart(measurements: List<PlantGrowthEntity>) {
    val heights = measurements.map { it.heightCm }
    if (heights.isEmpty()) return

    // Find min and max height for scaling
    val minHeight = heights.minOrNull() ?: 0.0
    val maxHeight = heights.maxOrNull() ?: 1.0 // Ensure at least 1.0 to prevent division by zero

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(Color.LightGray.copy(alpha = 0.2f)) // Light background for the chart area
    ) {
        val width = size.width
        val height = size.height
        val dataPoints = measurements.size
        if (dataPoints < 2) return@Canvas // Need at least two points to draw a line

        // X-axis scale (time)
        val xStep = width / (dataPoints - 1)

        // Y-axis scale (height)
        val yRange = (maxHeight - minHeight).toFloat()
        val yScale = if (yRange > 0) height / yRange else 0f

        // Draw line graph
        val path = Path().apply {
            // Move to the first point
            val firstX = 0f
            val firstY = height - (measurements.first().heightCm - minHeight).toFloat() * yScale
            moveTo(firstX, firstY)

            // Draw lines to subsequent points
            measurements.forEachIndexed { index, measurement ->
                if (index > 0) {
                    val x = index * xStep
                    val y = height - (measurement.heightCm - minHeight).toFloat() * yScale
                    lineTo(x, y)
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Green, // You could make this color dynamic based on overall performance
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw points for each measurement
        measurements.forEachIndexed { index, measurement ->
            val x = index * xStep
            val y = height - (measurement.heightCm - minHeight).toFloat() * yScale
            drawCircle(Color.Green, radius = 5.dp.toPx(), center = Offset(x, y))
        }
    }
}