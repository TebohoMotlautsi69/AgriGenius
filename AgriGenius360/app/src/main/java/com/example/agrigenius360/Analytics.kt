package com.example.agrigenius360

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints // Import Constraints


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(
    navController: NavHostController,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val availablePlantNames by viewModel.availablePlantNames.collectAsState()
    val selectedPlantNames by viewModel.selectedPlantNames.collectAsState()
    val comparisonResults by viewModel.comparisonResults.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showPlantSelectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plant Analytics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showPlantSelectionDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Plants to Compare")
            }
            Spacer(Modifier.height(8.dp))

            if (selectedPlantNames.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(selectedPlantNames) { plantName ->
                        InputChip(
                            selected = true,
                            onClick = { viewModel.removePlantFromSelection(plantName) },
                            label = { Text(plantName) },
                            trailingIcon = { Icon(Icons.Default.Info, contentDescription = "Deselect") }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (comparisonResults.isEmpty() && selectedPlantNames.isNotEmpty()) {
                Text("No data available for selected plants.", modifier = Modifier.padding(16.dp))
            } else if (comparisonResults.isEmpty() && selectedPlantNames.isEmpty()) {
                Text("Please select plants to view analytics.", modifier = Modifier.padding(16.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comparisonResults) { stats ->
                    PlantStatsCard(stats)
                }
                item {
                    if (comparisonResults.isNotEmpty()) {
                        StatisticsExplanation()
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (chartData.isNotEmpty()) {
                Text(
                    text = "Height vs. Average Growth Rate",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PlantGrowthChart(chartData)
            }

            // Plant Selection Dialog (remains unchanged)
            if (showPlantSelectionDialog) {
                AlertDialog(
                    onDismissRequest = { showPlantSelectionDialog = false },
                    title = { Text("Select Plants") },
                    text = {
                        Column {
                            availablePlantNames.forEach { name ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedPlantNames.contains(name),
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) {
                                                viewModel.addPlantToSelection(name)
                                            } else {
                                                viewModel.removePlantFromSelection(name)
                                            }
                                        }
                                    )
                                    Text(name)
                                }
                            }
                            if (availablePlantNames.isEmpty()) {
                                Text("No plants added yet. Add plants to see them here.")
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showPlantSelectionDialog = false }) {
                            Text("Done")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { viewModel.clearSelection(); showPlantSelectionDialog = false }) {
                            Text("Clear All")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlantStatsCard(stats: PlantComparisonStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Analysis for: ${stats.plantName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            Text(text = "Height Growth Stats:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            StatisticRow(label = "Mean", value = "%.2f cm".format(stats.heightStats.mean))
            StatisticRow(label = "Median", value = "%.2f cm".format(stats.heightStats.median))
            StatisticRow(label = "Mode", value = stats.heightStats.mode?.let { "%.2f cm".format(it) } ?: "N/A")
            Spacer(Modifier.height(8.dp))

            Text(text = "Average Growth Rate Stats:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            StatisticRow(label = "Mean", value = "%.2f cm/day".format(stats.growthRateStats.mean))
            StatisticRow(label = "Median", value = "%.2f cm/day".format(stats.growthRateStats.median))
            StatisticRow(label = "Mode", value = stats.growthRateStats.mode?.let { "%.2f cm/day".format(it) } ?: "N/A")
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontWeight = FontWeight.Normal, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun StatisticsExplanation() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Understanding the Statistics:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "• Mean (Average): The sum of all values divided by the number of values. It gives a general idea of the central tendency.",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "• Median: The middle value in a sorted dataset. It is less affected by extreme outliers than the mean.",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "• Mode: The value that appears most frequently in a dataset. Useful for identifying common occurrences.",
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun PlantGrowthChart(chartPoints: List<ChartPoint>) {
    val textMeasurer = rememberTextMeasurer()

    val plantColors = remember {
        mutableStateMapOf<String, Color>().apply {
            val distinctPlantNames = chartPoints.map { it.plantName }.distinct()
            val availableColors = listOf(
                Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan,
                Color.Yellow, Color.DarkGray, Color.LightGray, Color.Gray,
                Color.Red.copy(alpha = 0.7f), Color.Blue.copy(alpha = 0.7f), Color.Green.copy(alpha = 0.7f)
            )
            distinctPlantNames.forEachIndexed { index, name ->
                this[name] = availableColors[index % availableColors.size]
            }
        }
    }
    val padding = 32.dp.value
    val axisLabelOffset = 16.dp.value
    val pointRadius = 6.dp.value

    val maxHeights = chartPoints.maxOfOrNull { it.height } ?: 1.0
    val maxGrowthRates = chartPoints.maxOfOrNull { it.growthRate } ?: 1.0

    val plotMaxX = maxHeights * 1.1
    val plotMaxY = maxGrowthRates * 1.1

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        val chartWidth = size.width - padding * 2 - axisLabelOffset
        val chartHeight = size.height - padding * 2 - axisLabelOffset

        if (chartWidth <= 0 || chartHeight <= 0) {
            return@Canvas
        }
        val xScale = chartWidth / plotMaxX
        val yScale = chartHeight / plotMaxY

        val plotOriginX = padding + axisLabelOffset
        val plotOriginY = size.height - padding - axisLabelOffset
        drawLine(
            color = Color.Black,
            start = Offset(plotOriginX, plotOriginY),
            end = Offset(plotOriginX + chartWidth, plotOriginY),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Black,
            start = Offset(plotOriginX, plotOriginY),
            end = Offset(plotOriginX, plotOriginY - chartHeight),
            strokeWidth = 2f
        )
        val xLabelCount = 5
        for (i in 0..xLabelCount) {
            val xValue = (plotMaxX / xLabelCount) * i
            val xPx = plotOriginX + (xValue * xScale).toFloat()
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(xPx, plotOriginY),
                end = Offset(xPx, plotOriginY - chartHeight),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // Draw label
            val textLayoutResult = textMeasurer.measure(
                text = "%.1f".format(xValue),
                style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "%.1f".format(xValue),
                topLeft = Offset(xPx - textLayoutResult.size.width / 2, plotOriginY + 5.dp.toPx()),
                style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
            )
        }
        val yLabelCount = 5
        for (i in 0..yLabelCount) {
            val yValue = (plotMaxY / yLabelCount) * i
            val yPx = plotOriginY - (yValue * yScale).toFloat()
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(plotOriginX, yPx),
                end = Offset(plotOriginX + chartWidth, yPx),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            val textLayoutResult = textMeasurer.measure(
                text = "%.1f".format(yValue),
                style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "%.1f".format(yValue),
                topLeft = Offset(plotOriginX - textLayoutResult.size.width - 5.dp.toPx(), yPx - textLayoutResult.size.height / 2),
                style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
            )
        }

        val heightTextLayoutResult = textMeasurer.measure(
            text = "Height (cm)",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "Height (cm)",
            topLeft = Offset(plotOriginX + chartWidth / 2 - heightTextLayoutResult.size.width / 2, size.height - 10.dp.toPx()),
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )

        val growthRateTextLayoutResult = textMeasurer.measure(
            text = "Growth Rate (cm/day)",
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "Growth Rate (cm/day)",
            topLeft = Offset(10.dp.toPx(), plotOriginY - chartHeight / 2 + growthRateTextLayoutResult.size.width / 2),
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )

        chartPoints.forEach { point ->
            val x = plotOriginX + (point.height * xScale).toFloat()
            val y = plotOriginY - (point.growthRate * yScale).toFloat()

            drawCircle(
                color = plantColors[point.plantName] ?: Color.Black,
                center = Offset(x, y),
                radius = pointRadius,
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = plantColors[point.plantName]?.copy(alpha = 0.3f) ?: Color.Black.copy(alpha = 0.3f),
                center = Offset(x, y),
                radius = pointRadius - 2f,
            )
        }
        val legendSpacing = 20.dp.toPx()
        val itemSpacing = 10.dp.toPx()
        val circleSize = pointRadius * 2
        val textPadding = 5.dp.toPx()

        val legendStartX = plotOriginX + chartWidth + legendSpacing

        val maxTextWidthForLegend = (size.width - legendStartX - circleSize - textPadding).coerceAtLeast(0f)

        var legendCurrentY = plotOriginY - chartHeight + legendSpacing

        plantColors.forEach { (plantName, color) ->
            drawCircle(
                color = color,
                center = Offset(legendStartX + pointRadius, legendCurrentY + pointRadius),
                radius = pointRadius
            )

            val textX = legendStartX + circleSize + textPadding
            val textY = legendCurrentY

            if (maxTextWidthForLegend > 0) {
                val textLayoutResult = textMeasurer.measure(
                    text = plantName,
                    style = TextStyle(fontSize = 12.sp, color = Color.Black),
                    constraints = Constraints(maxWidth = maxTextWidthForLegend.toInt())
                )
                val safeTextX = textX.coerceAtMost(size.width - textLayoutResult.size.width)
                if (safeTextX >= 0) { // Also ensure it's not trying to draw off the left edge
                    drawText(
                        textMeasurer = textMeasurer,
                        text = plantName,
                        topLeft = Offset(safeTextX, textY),
                        style = TextStyle(fontSize = 12.sp, color = Color.Black)
                    )
                }
            } else {
            }
            legendCurrentY += (circleSize + itemSpacing)
        }
    }
}