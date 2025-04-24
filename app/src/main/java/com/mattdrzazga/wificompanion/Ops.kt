package com.mattdrzazga.wificompanion

import android.os.Bundle
import com.mattdrzazga.wificompanion.MainActivity.Companion.CLEAR_DEVICE_ADMIN
import com.mattdrzazga.wificompanion.MainActivity.Companion.FORGET_NETWORK
import com.mattdrzazga.wificompanion.MainActivity.Companion.JOIN_NETWORK
import com.mattdrzazga.wificompanion.MainActivity.Companion.START_KEEP_ADB_WIFI_ON
import com.mattdrzazga.wificompanion.MainActivity.Companion.STOP_KEEP_ADB_WIFI_ON

sealed interface Ops {

    data object ClearDeviceAdmin : Ops

    data object StartAdbWifiKeeper : Ops

    data object StopAdbWifiKeeper : Ops

    data class ForgetNetwork(val ssid: String) : Ops {

        val isValid = ssid.isNotBlank()

        val error: String = if (isValid) {
            ""
        } else {
            EMPTY_SSID_ERROR
        }

        companion object {
            private const val EMPTY_SSID_ERROR = "SSID must not be empty!"
        }
    }

    data class JoinNetwork(val ssid: String, val password: String, val security: String) : Ops

    data object NoOp : Ops

    companion object {

        private const val ARG_SSID = "ssid"
        private const val ARG_PASSWORD = "password"
        private const val ARG_SECURITY = "security"

        fun fromIntent(bundle: Bundle?): Ops = when {
            bundle == null -> NoOp
            bundle.containsKey(CLEAR_DEVICE_ADMIN) -> ClearDeviceAdmin
            bundle.containsKey(START_KEEP_ADB_WIFI_ON) -> StartAdbWifiKeeper
            bundle.containsKey(STOP_KEEP_ADB_WIFI_ON) -> StopAdbWifiKeeper
            bundle.containsKey(FORGET_NETWORK) -> extractForgetNetworkOps(bundle)
            bundle.containsKey(JOIN_NETWORK) -> extractJoinNetworkOps(bundle)
            else -> NoOp
        }

        private fun extractForgetNetworkOps(bundle: Bundle): ForgetNetwork =
            ForgetNetwork(bundle.getString(FORGET_NETWORK, ""))

        private fun extractJoinNetworkOps(bundle: Bundle): JoinNetwork =
            JoinNetwork(
                ssid = bundle.getString(ARG_SSID, ""),
                password = bundle.getString(ARG_PASSWORD, ""),
                security = bundle.getString(ARG_SECURITY, ""),
            )
    }
}