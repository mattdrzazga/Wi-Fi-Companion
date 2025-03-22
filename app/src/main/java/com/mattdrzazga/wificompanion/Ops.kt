package com.mattdrzazga.wificompanion

import android.os.Bundle
import com.mattdrzazga.wificompanion.MainActivity.Companion.CLEAR_DEVICE_ADMIN
import com.mattdrzazga.wificompanion.MainActivity.Companion.FORGET_NETWORK
import com.mattdrzazga.wificompanion.MainActivity.Companion.JOIN_NETWORK
import com.mattdrzazga.wificompanion.MainActivity.Companion.START_KEEP_ADB_WIFI_ON
import com.mattdrzazga.wificompanion.MainActivity.Companion.STOP_KEEP_ADB_WIFI_ON

interface Ops {

    data object ClearDeviceAdmin : Ops

    data object StartAdbWifiKeeper : Ops

    data object StopAdbWifiKeeper : Ops

    data object ForgetNetwork : Ops

    data object JoinNetwork : Ops

    data object NoOp : Ops

    companion object {

        fun fromIntent(bundle: Bundle): Ops = when {
            bundle.containsKey(CLEAR_DEVICE_ADMIN) -> ClearDeviceAdmin
            bundle.containsKey(START_KEEP_ADB_WIFI_ON) -> StartAdbWifiKeeper
            bundle.containsKey(STOP_KEEP_ADB_WIFI_ON) -> StopAdbWifiKeeper
            bundle.containsKey(FORGET_NETWORK) -> ForgetNetwork
            bundle.containsKey(JOIN_NETWORK) -> JoinNetwork
            else -> NoOp
        }
    }
}