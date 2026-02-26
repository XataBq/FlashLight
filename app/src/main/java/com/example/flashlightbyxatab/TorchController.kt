package com.example.flashlightbyxatab

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log

class TorchController(context: Context) {
    private val appContext = context.applicationContext
    private val cameraManager = appContext.getSystemService(CameraManager::class.java)

    private val torchCameraId: String? by lazy { findTorchCameraId() }
    private var callback: CameraManager.TorchCallback? = null
    private var isRegistered: Boolean = false

    fun isTorchSupported(): Boolean = torchCameraId != null

    fun setEnabled(enabled: Boolean) {
        val id = torchCameraId ?: return
        try {
            cameraManager.setTorchMode(id, enabled)
            Log.d("torch mode", "torchMode $enabled")
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

        val cb = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                if (cameraId == id) onModeChanged(enabled)
            }

            override fun onTorchModeUnavailable(cameraId: String) {
                if (cameraId == id) onUnavailable()
            }
        }

        callback = cb
        try {
            cameraManager.registerTorchCallback(cb, Handler(Looper.getMainLooper()))
            Log.d("callback", "callback registered")
            isRegistered = true
        } catch (t: Throwable) {
            callback = null
            isRegistered = false
            onError(t)
        }
    }

    fun unregisterTorchCallback() {
        val cb = callback ?: return
        try {
            cameraManager.unregisterTorchCallback(cb)
            Log.d("callback", "callback unregistered")

        } catch (_: Throwable) {
            // пусто
        } finally {
            callback = null
            isRegistered = false
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