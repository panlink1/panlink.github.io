package com.panlink.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SavedWifi(val ssid: String, val password: String)

class WifiStore(context: Context) {
    private val prefs = context.getSharedPreferences("panlink", Context.MODE_PRIVATE)

    fun getAll(): List<SavedWifi> {
        val arr = JSONArray(prefs.getString("wifi", "[]"))
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            SavedWifi(o.getString("ssid"), o.getString("password"))
        }
    }

    fun saveAll(list: List<SavedWifi>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("ssid", it.ssid).put("password", it.password))
        }
        prefs.edit().putString("wifi", arr.toString()).apply()
    }
}