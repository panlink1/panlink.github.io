package com.panlink.app

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion

object WifiConnector {
    fun connect(context: Context, wifi: SavedWifi): String {
        val wm = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager

        val builder = WifiNetworkSuggestion.Builder().setSsid(wifi.ssid)
        if (wifi.password.isNotEmpty()) builder.setWpa2Passphrase(wifi.password)
        val suggestion = builder.build()

        wm.removeNetworkSuggestions(listOf(suggestion))
        return when (wm.addNetworkSuggestions(listOf(suggestion))) {
            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS ->
                "Berhasil. Setujui notifikasi WiFi dari PanLink jika muncul."
            WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED ->
                "Ditolak. Izinkan PanLink di pengaturan WiFi HP."
            else -> "Gagal menambahkan WiFi."
        }
    }
}