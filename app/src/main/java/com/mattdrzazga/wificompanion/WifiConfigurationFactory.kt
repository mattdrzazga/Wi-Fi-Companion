package com.mattdrzazga.wificompanion

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiEnterpriseConfig.Phase2

// TODO Check if assigning static BSSID fixes disconnection issues.
// TODO Add support to hidden networks.
class WifiConfigurationFactory {

    fun create(ssid: String, security: String, password: String?): WifiConfiguration {
        return when (security) {
            "WEP" -> createWep(ssid, password.orEmpty())
            "WPA" -> createWpa(ssid, password.orEmpty())
            else -> createUnsecured(ssid)
        }
    }

    // Security None
    private fun createUnsecured(ssid: String) = WifiConfiguration().apply {
        SSID = "\"$ssid\""

        status = WifiConfiguration.Status.ENABLED
        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        allowedAuthAlgorithms.clear()
    }

    // Security WEP
    private fun createWep(ssid: String, password: String) = WifiConfiguration().apply {
        SSID = "\"$ssid\""
        wepKeys[0] = "\"$password\""
        wepTxKeyIndex = 0
        status = WifiConfiguration.Status.ENABLED
        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
    }

    // Security WPA WPA2-Personal
    private fun createWpa(ssid: String, password: String) = WifiConfiguration().apply {
        SSID = "\"$ssid\""
        preSharedKey = "\"$password\""
        status = WifiConfiguration.Status.ENABLED
        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
        allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)

        allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
    }

    // Security WPA WPA2-Enterprise
    private fun createWPA2EAndEAP(ssid: String, password: String) = WifiConfiguration().apply {
        SSID = "\"$ssid\""
        preSharedKey = "\"$password\""

        allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)

        allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)

        enterpriseConfig.identity = "identity"
        enterpriseConfig.anonymousIdentity = "anonymousIdentity"
        enterpriseConfig.password = "password"
        enterpriseConfig.eapMethod = WifiEnterpriseConfig.Eap.PEAP
        enterpriseConfig.phase2Method = Phase2.MSCHAPV2
    }
}