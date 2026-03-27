package com.gamehivecorp.taptita.gray4.device_props.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class WifiInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val undefined = "undefined"
            val context = args[0] as Context
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                ?: return "WIFI[0,$undefined]"

            val wifiInfo = wifiManager.connectionInfo ?: return "WIFI[0,$undefined]"

            val rssi = wifiInfo.rssi
            val frequency = wifiInfo.frequency
            val linkSpeed = wifiInfo.linkSpeed
            val wifiStandard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                wifiInfo.wifiStandard
            } else {
                -1
            }
            val maxSpeed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                wifiInfo.maxSupportedTxLinkSpeedMbps
            } else {
                -1
            }
            val dhcp = wifiManager.dhcpInfo
            val gateway = dhcp?.gateway ?: 0
            val netmask = dhcp?.netmask ?: 0
            "WIFI[ok,rssi=$rssi,freq=$frequency,speed=$linkSpeed,std=$wifiStandard,max=$maxSpeed,gw=$gateway,mask=$netmask]"
        } catch (e: Throwable) {
            "WIFI[error,${e.message}]"
        }
    }
}