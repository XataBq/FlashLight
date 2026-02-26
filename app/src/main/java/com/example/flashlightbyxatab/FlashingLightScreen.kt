package com.example.flashlightbyxatab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FlashingLightScreen(
    vm: TorchViewModel = viewModel()
) {


    val state by vm.torchUiState.collectAsState()

    val title = when(state){
        TorchUiState.Off -> "Фонарик выключен"
        TorchUiState.On ->"Фонарик включен"
        TorchUiState.NotSupported -> "Вспышка не поддерживается"
        TorchUiState.Unavailable -> "Фонарик недоступен(камера занята/ограничение)"
        is TorchUiState.Error -> "Ошибка фонарика"
    }

    val buttonText = when (state) {
        TorchUiState.On -> "Выключить"
        TorchUiState.Off -> "Включить"
        else -> "Включить"
    }

    val enabled = state == TorchUiState.On || state == TorchUiState.Off

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        if (state is TorchUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = (state as TorchUiState.Error).message,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = enabled,
            onClick = { vm.onToggleClicked() }
        ) {
            Text(buttonText)
        }
    }
}