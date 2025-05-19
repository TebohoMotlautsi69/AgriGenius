package com.example.agrigenius360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
            border = BorderStroke(2.dp, Color(0xFF008000)), // green border
            color  = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = "Hi Username User,\nHow can I help\nyou today?",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 30.dp, top = 100.dp, bottom = 100.dp)
            )
        }

        /** Spacer to mimic RN paddingTop = 100 */
        Spacer(modifier = Modifier.height(100.dp))

        /** Three feature tiles */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            FeatureTile(
                drawableId = R.drawable.growth,
                onClick    = {}
            )

            FeatureTile(
                drawableId = R.drawable.moisture,
                onClick    = {}
            )

            FeatureTile(
                drawableId = R.drawable.barchart,
                onClick    = {}
            )
        }
    }
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