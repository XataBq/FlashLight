package com.example.flashlightbyxatab

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun FlashingLightScreen() {
    val context = LocalContext.current
    val viewModel = ViewModel(context)

    Button(
        onClick = viewModel::flashOnOff,
        modifier = Modifier
            .size(100.dp, 50.dp),
        shape = RoundedCornerShape(13.dp),
    ) {
        Text(
            text = if (uiState.switched) "On" else "Off"
        )
    }
}