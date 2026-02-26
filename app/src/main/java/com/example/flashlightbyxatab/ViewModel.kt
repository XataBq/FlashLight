package com.example.flashlightbyxatab

import android.content.Context
import android.graphics.Camera
import android.hardware.camera2.CameraManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ViewModel(
    context: Context
): ViewModel() {
    private val torch = TorchController()
    private val _torchState = MutableStateFlow<TorchUiState>(TorchUiState.Off)
}