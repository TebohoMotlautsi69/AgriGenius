package com.example.agrigenius360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlantGrowthCalculatorScreen() {

    var initialHeight by remember { mutableStateOf("") }
    var finalHeight   by remember { mutableStateOf("") }
    var days          by remember { mutableStateOf("") }
    var growthRate    by remember { mutableStateOf<Float?>(null) }
    var showResult    by remember { mutableStateOf(false) }

    fun calculate() {
        val init = initialHeight.toFloatOrNull()
        val fin  = finalHeight.toFloatOrNull()
        val d    = days.toFloatOrNull()

        if (init != null && fin != null && d != null && d > 0f) {
            growthRate = (fin - init) / d
            showResult = true
        } else {
            growthRate = null
            showResult = false
        }
    }

    /* ---------- UI ---------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FEF5))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("🌿 Plant Growth Rate", fontSize = 20.sp, modifier = Modifier.padding(12.dp))


        LabeledNumberField("Initial Height (cm)", initialHeight) { initialHeight = it }
        LabeledNumberField("Final  Height (cm)",  finalHeight)   { finalHeight = it }
        LabeledNumberField("Number of Days",      days)          { days = it }

        Button(
            onClick = ::calculate,
            colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F38)),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(top = 24.dp)
        ) {
            Text("Calculate Growth Rate", color = Color.White, fontSize = 18.sp)
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
