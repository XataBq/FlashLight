package com.example.flashlightbyxatab

sealed interface TorchUiState {
    data object NotSupported: TorchUiState
    data object Off: TorchUiState
    data object On: TorchUiState
    data object Unavailable: TorchUiState
    data class Error(val message: String): TorchUiState
}
