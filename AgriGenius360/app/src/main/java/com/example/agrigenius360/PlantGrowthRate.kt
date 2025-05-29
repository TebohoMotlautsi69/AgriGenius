package com.example.agrigenius360

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlantGrowthCalculatorScreen(
    plantDAO: PlantDAO,
    plantGrowthDAO: PlantGrowthDAO,
    navController: NavController,
    plantId: Int
) {
    var initialHeight by remember { mutableStateOf("") }
    var finalHeight by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }
    var growthRate by remember { mutableStateOf<Double?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var comparisonMessage by remember { mutableStateOf<String?>(null) }

    var plant by remember { mutableStateOf<PlantEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(plantId) {
        if (plantId !=0){
            plant = plantDAO.getPlantById(plantId)
        }
    }
    val optimalRate = plant?.optimalGrowthRateCmPerDay?.toDouble() ?: 0.0

    fun calculateAndSaveGrowthAction() {
        val init = initialHeight.toFloatOrNull()
        val fin = finalHeight.toFloatOrNull()
        val d = days.toFloatOrNull()

        errorMessage = null
        comparisonMessage = null

        if (init == null || fin == null || d == null) {
            errorMessage = "Please enter valid numbers for all fields."
            showResult = false
            return
        }

        if (d <= 0f) {
            errorMessage = "Number of days must be greater than zero."
            showResult = false
            return
        }

        val calculatedRate = (fin - init) / d
        growthRate = calculatedRate.toDouble()
        showResult = true

        if (optimalRate > 0) {
            when {
                calculatedRate >= optimalRate * 1.05f -> {
                    comparisonMessage = "Excellent! Growth has improved beyond optimal."
                }
                calculatedRate >= optimalRate * 0.95f -> {
                    comparisonMessage = "Good! Growth is on track with optimal."
                }
                else -> {
                    comparisonMessage = "Warning: Growth is below optimal."
                }
            }
        } else {
            comparisonMessage = "Optimal growth rate not defined for this plant."
        }

        coroutineScope.launch {
            try {

                if (plant == null) {
                    errorMessage = "Error: Plant data not loaded. Please try again."
                    return@launch
                }

                val newRecord = PlantGrowthEntity(
                    plantId = plantId,
                    heightCm = fin.toDouble(),
                    initialHeight = init,
                    finalHeight = fin,
                    days = d,
                    growthRate = calculatedRate,
                    cropType = plant!!.type,
                    plantName = plant!!.name
                )
                plantGrowthDAO.insertMeasurement(newRecord)
                errorMessage = "Growth record saved successfully!"
                initialHeight =""
                finalHeight=""
                days=""
                showResult = true

            } catch (e: Exception) {
                errorMessage = "Error saving growth record: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FEF5))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("🌿 Plant Growth Rate Calculator", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        plant?.let {
            Text("Calculating for: ${it.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Optimal Rate: ${"%.2f".format(it.optimalGrowthRateCmPerDay)} cm/day", fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        } ?: run {
            CircularProgressIndicator() // Show loading if plant is null initially
            Text("Loading plant details...", modifier = Modifier.padding(bottom = 16.dp))
        }

        LabeledNumberField("Initial Height (cm)", initialHeight) { initialHeight = it }
        LabeledNumberField("Final Height (cm)",  finalHeight)   { finalHeight = it }
        LabeledNumberField("Number of Days",      days)          { days = it }

        Button(
            onClick = { calculateAndSaveGrowthAction() },
            colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F38)),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(top = 24.dp)
        ) {
            Text("Calculate & Save Growth", color = Color.White, fontSize = 18.sp)
        }

        errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        AnimatedVisibility(
            visible = showResult && growthRate != null,
            enter   = slideInVertically { it } + fadeIn(),
            exit    = slideOutVertically { it } + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF087F38))
            ) {
                Text(
                    text = "📈 Growth Rate: ${growthRate?.let { "%.2f".format(it) }} cm/day",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("home") {popUpTo("home") { inclusive = true} } },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF087F38)),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Go to Home", fontSize = 16.sp)
        }
    }
}

@Composable
private fun LabeledNumberField(label: String, value: String, onChange: (String) -> Unit) {
    Text(label, fontSize = 18.sp, modifier = Modifier.padding(top = 20.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        singleLine = true
    )
}

//@Composable
//private fun LabeledInputField(label: String, value: String, onChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
//    Text(label, fontSize = 18.sp, modifier = Modifier.padding(top = 20.dp))
//    OutlinedTextField(
//        value = value,
//        onValueChange = onChange,
//        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 8.dp),
//        singleLine = true
//    )
//}