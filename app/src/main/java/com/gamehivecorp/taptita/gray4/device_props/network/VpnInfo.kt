package com.gamehivecorp.taptita.gray4.device_props.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class VpnInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val context = args[0] as Context
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            val hasVpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
            "VPN[$hasVpn]"
        } catch (e: Throwable) {
            "VPN[undefined]"
        }
    }
}
