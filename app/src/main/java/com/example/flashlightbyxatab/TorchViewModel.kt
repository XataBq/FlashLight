package com.example.flashlightbyxatab

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TorchViewModel(app: Application): AndroidViewModel(app) {
    private val controller = TorchController(app)
    private val _torchUiState = MutableStateFlow<TorchUiState>(TorchUiState.Off)
    val torchUiState: StateFlow<TorchUiState> = _torchUiState.asStateFlow()

    init {
        if (!controller.isTorchSupported()) {
            _torchUiState.value = TorchUiState.NotSupported
        }

        controller.registerTorchCallback(
            onModeChanged = { enabled ->
                _torchUiState.value = if(enabled) TorchUiState.On else TorchUiState.Off
            },
            onUnavailable = {
                _torchUiState.value = TorchUiState.Unavailable
            },
            onError = { t->
                _torchUiState.value = TorchUiState.Error(t.message ?: "Torch callback error")
            }
        )
    }

    fun onToggleClicked() {
        when (_torchUiState.value) {
            TorchUiState.NotSupported -> return
            TorchUiState.Unavailable -> {
                controller.setEnabled(true)
            }
            is TorchUiState.Error -> {
                controller.setEnabled(false)
            }

            TorchUiState.On -> controller.setEnabled(false)
            TorchUiState.Off -> controller.setEnabled(true)
        }
    }

    fun turnOff() {
        controller.setEnabled(false)
    }

    override fun onCleared() {
        controller.unregisterTorchCallback()
        super.onCleared()
    }
}