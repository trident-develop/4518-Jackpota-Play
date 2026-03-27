package com.gamehivecorp.taptita.gray4.device_props.core

import android.content.Context
import com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotion
import com.gamehivecorp.taptita.gray4.device_props.network.SimInfo
import com.gamehivecorp.taptita.gray4.device_props.network.VpnInfo
import com.gamehivecorp.taptita.gray4.device_props.network.ProxyInfo
import com.gamehivecorp.taptita.gray4.device_props.network.WifiInfo
import com.gamehivecorp.taptita.gray4.device_props.network.TimezoneInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.GyroscopeInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.AccelerometerInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.LightSensorInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.AudioVolumeInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.MagnetometerInfo
import com.gamehivecorp.taptita.gray4.device_props.sensor.ProximitySensorInfo
import com.gamehivecorp.taptita.gray4.device_props.system.WidevineInfo
import com.gamehivecorp.taptita.gray4.device_props.system.CpuInfo
import com.gamehivecorp.taptita.gray4.device_props.system.BuildInfo
import com.gamehivecorp.taptita.gray4.device_props.system.BatteryInfo
import com.gamehivecorp.taptita.gray4.device_props.system.UptimeInfo
import com.gamehivecorp.taptita.gray4.device_props.system.BrightnessInfo
import com.gamehivecorp.taptita.gray4.device_props.system.InstallSourceInfo
import com.gamehivecorp.taptita.gray4.device_props.system.AccessibilityInfo
import com.gamehivecorp.taptita.gray4.device_props.system.RootInfo

/**
 * Збір device properties результатів по категоріях.
 * 
 * Категорії:
 * x4 - Network & Security
 * x5 - Sensors
 * x7 - Empty (зарезервовано)
 * x8 - Device ID
 * x9 - Build & CPU Info
 * x10 - System Info
 * s28 - App Info
 * s30 - Accessibility
 * 
 * Використання:
 * val result = DevicePropertiesResult.create(context)
 * result.getX4() // "SIM[...];VPN[...];..."
 */
class DevicePropertiesResult private constructor(
    // x4 - Network & Security
    private val sim: String,
    private val vpn: String,
    private val proxy: String,
    private val wifi: String,
    private val timezone: String,
    private val root: String,

    // x5 - Sensors
    private val gyroscope: String,
    private val accelerometer: String,
    private val lightSensor: String,
    private val audioVolume: String,
    private val magnetometer: String,
    private val proximity: String,
    
    // x8 - Device ID
    private val widevineId: String,
    
    // x9 - CPU
    private val cpuHash: String,
    
    // x10 - BUILD
    private val buildInfo: String,
    
    // s28 - CHRG, UP, BRIGHT
    private val charging: String,
    private val uptime: String,
    private val brightness: String,
    
    // s30 - INSTALL, A11Y
    private val installSource: String,
    private val accessibility: String
) {
    
    companion object {
        /**
         * Створює DevicePropertiesResult та збирає всі дані.
         * ВАЖЛИВО:
         * - не викликати на main thread
         * - не забудьте замінити "com.gamehivecorp.taptita.gray4.device_props" пакедж на свій
         * - не забудьте додати ACCESS_WIFI_STATE пермішн в маніфест
         * - IpScore викликати окремо лише один раз за встановлення застосунку
         */
        suspend fun create(context: Context): DevicePropertiesResult {
            val motionResult = DeviceMotion.getResult(context)
            return DevicePropertiesResult(
                // x4 - Network & Security
                sim = SimInfo().collect(context),
                vpn = VpnInfo().collect(context),
                proxy = ProxyInfo().collect(context),
                wifi = WifiInfo().collect(context),
                timezone = TimezoneInfo().collect(),
                root =  RootInfo().collect(context),
                
                // x5 - Sensors
                gyroscope = GyroscopeInfo().collect(motionResult),
                accelerometer = AccelerometerInfo().collect(motionResult),
                lightSensor = LightSensorInfo().collect(motionResult),
                audioVolume = AudioVolumeInfo().collect(context),
                magnetometer = MagnetometerInfo().collect(motionResult),
                proximity = ProximitySensorInfo().collect(motionResult),
                
                // x8 - Device ID
                widevineId = WidevineInfo().collect(context),
                
                // x9 - CPU
                cpuHash = CpuInfo().collect(context),
                
                // x10 - BUILD
                buildInfo = BuildInfo().collect(context),
                
                // s28 - CHRG, UP, BRIGHT
                charging = BatteryInfo().collect(context),
                uptime = UptimeInfo().collect(context),
                brightness = BrightnessInfo().collect(context),
                
                // s30 - INSTALL, A11Y
                installSource = InstallSourceInfo().collect(context),
                accessibility = AccessibilityInfo().collect(context)
            )
        }
    }
    
    /**
     * x4 - Network & Security
     * @return "SIM[value];VPN[value];PROXY[value];WIFI[value];TZ[value];ROOT[value]"
     */
    fun getX4(): String = listOf(sim, vpn, proxy, wifi, timezone, root)
        .filter { it.isNotBlank() }
        .joinToString(";")
    
    /**
     * x5 - Sensors
     * @return "GYRO[value];ACC[value];LIGHT[value];MAGN[value];PROXIMITY[value]"
     */
    fun getX5(): String = listOf(gyroscope, accelerometer, lightSensor, magnetometer, proximity)
        .filter { it.isNotBlank() }
        .joinToString(";")
    
    /**
     * x7 - Reserved
     */
    fun getX7(): String = ""
    
    /**
     * x8 - Device ID
     * @return "WID[value]"
     */
    fun getX8(): String = widevineId
    
    /**
     * x9 - CPU
     * @return "CPU[value]"
     */
    fun getX9(): String = cpuHash
    
    /**
     * x10 - BUILD
     * @return "BUILD[value]"
     */
    fun getX10(): String = buildInfo
    
    /**
     * s28 - CHRG, UP, BRIGHT
     * @return "CHRG[value];UP[value];BRIGHT[value];VOL[value]"
     */
    fun getS28(): String = listOf(charging, uptime, brightness, audioVolume)
        .filter { it.isNotBlank() }
        .joinToString(";")
    
    /**
     * s30 - INSTALL, A11Y
     * @return "INSTALL[value];A11Y[value]"
     */
    fun getS30(): String = listOf(installSource, accessibility)
        .filter { it.isNotBlank() }
        .joinToString(";")
}
