package com.example.flashlightbyxatab

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class TorchController(context: Context) {
    private val appContext = context.applicationContext
    private val cameraManager = appContext.getSystemService(CameraManager::class.java)

    private val torchCameraId: String? by lazy { findTorchCameraId() }
    private val callback: CameraManager.TorchCallback? = null
    private var isRegistered: Boolean = false

    val isTorchSupported: Boolean = torchCameraId != null

    fun setEnabled(enabled: Boolean) {
        val id = torchCameraId ?: return
        try {
            cameraManager.setTorchMode(id, enabled)
        } catch (e: CameraAccessException) {
            //камера недоступна / занята / системная ошибка
        } catch (e: SecurityException) {
            // Нет CAMERA permission
        } catch (e: IllegalArgumentException) {
            // Неверны cameraId
        }
    }

    fun registerTorchCallback(
        onModeChanged: (enabled: Boolean) -> Unit,
        onUnavailable: () -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        if (isRegistered) return
        val id = torchCameraId ?: run {
            onUnavailable()
            return
        }
    }

    private fun findTorchCameraId(): String? {
        val ids = try {
            cameraManager.cameraIdList
        } catch (_: Throwable) {
            return null
        }

        var fallbackAnyFlash: String? = null

        for (id in ids) {
            val chars = try {
                cameraManager.getCameraCharacteristics(id)
            } catch (_: Throwable) {
                continue
            }

            val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            if (!hasFlash) continue

            val lensFacing = chars.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id
            }

            if (fallbackAnyFlash == null) fallbackAnyFlash = id
        }

        return fallbackAnyFlash
    }
}