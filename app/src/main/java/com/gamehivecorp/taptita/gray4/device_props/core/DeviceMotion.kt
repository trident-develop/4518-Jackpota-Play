package com.gamehivecorp.taptita.gray4.device_props.core

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

object DeviceMotion {

    private const val WARMUP_DURATION_MILLIS = 100L
    private const val MEASURE_DURATION_MILLIS = 950L
    private const val GRAVITY_FILTER_ALPHA = 0.9f

    suspend fun getResult(context: Context): com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotionResult {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            ?: return _root_ide_package_.com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotionResult(
                null,
                null,
                null,
                null,
                null
            )
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        val accelMagnitudes = ArrayList<Float>(512)
        val gyroMagnitudes = ArrayList<Float>(512)

        val gravity = FloatArray(3)
        var gravityInitialized = false

        var lastLight = -1f
        var lastProx = -1f
        var lastMagMagnitude = -1f

        var collecting = true
        var isWarmingUp = true

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (!collecting) return // Знімаємо перевірку isWarmingUp для всіх подій
                try {
                    val v = event.values
                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            if (isWarmingUp) return // Для Accel/Gyro прогрів залишаємо для точності Jitter
                            if (!gravityInitialized) {
                                gravity[0] = v[0]; gravity[1] = v[1]; gravity[2] = v[2]
                                gravityInitialized = true
                                return
                            }
                            gravity[0] = GRAVITY_FILTER_ALPHA * gravity[0] + (1 - GRAVITY_FILTER_ALPHA) * v[0]
                            gravity[1] = GRAVITY_FILTER_ALPHA * gravity[1] + (1 - GRAVITY_FILTER_ALPHA) * v[1]
                            gravity[2] = GRAVITY_FILTER_ALPHA * gravity[2] + (1 - GRAVITY_FILTER_ALPHA) * v[2]
                            accelMagnitudes.add(sqrt((v[0]-gravity[0])*(v[0]-gravity[0]) + (v[1]-gravity[1])*(v[1]-gravity[1]) + (v[2]-gravity[2])*(v[2]-gravity[2])))
                        }
                        Sensor.TYPE_GYROSCOPE -> {
                            if (isWarmingUp) return
                            gyroMagnitudes.add(sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]))
                        }
                        // Ці датчики оновлюємо ЗАВЖДИ, навіть під час прогріву
                        Sensor.TYPE_MAGNETIC_FIELD -> lastMagMagnitude = sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2])
                        Sensor.TYPE_LIGHT -> lastLight = v[0]
                        Sensor.TYPE_PROXIMITY -> lastProx = v[0]
                    }
                } catch (_: Throwable) { }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        listOfNotNull(accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor)
            .forEach { sensor ->
                try {
                    sensorManager.registerListener(
                        listener,
                        sensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                } catch (_: Throwable) { }
            }

        try {
            delay(WARMUP_DURATION_MILLIS)
            isWarmingUp = false
            delay(MEASURE_DURATION_MILLIS)
        } finally {
            collecting = false
            try { sensorManager.unregisterListener(listener) }
            catch (_: Throwable) { }
        }

        return DeviceMotionResult(
            accelScore = formatScore(accelMagnitudes),
            gyroScore = formatScore(gyroMagnitudes),
            lightScore = formatValue(lastLight),
            proxScore = formatValue(lastProx),
            magScore = formatValue(lastMagMagnitude)
        )
    }

    private fun formatScore(samples: List<Float>): String? =
        try {
            if (samples.size < 2) "0.000"
            else String.format(Locale.US, "%.3f", calculateMotionScore(samples))
        } catch (_: Throwable) {
            null
        }

    private fun formatValue(value: Float): String? =
        if (!value.isFinite() || value < 0f) null
        else String.format(Locale.US, "%.2f", value)

    private fun calculateMotionScore(samples: List<Float>): Float {
        val diffs = FloatArray(samples.size - 1) { i -> abs(samples[i+1] - samples[i]) }.apply { sort() }
        val p50 = if (diffs.isEmpty()) 0f else diffs[(diffs.size * 0.5).toInt()]
        val p90 = if (diffs.isEmpty()) 0f else diffs[(diffs.size * 0.9).toInt()]
        return p50 + p90
    }
}
