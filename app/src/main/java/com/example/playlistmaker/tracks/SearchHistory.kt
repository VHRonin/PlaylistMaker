package com.example.playlistmaker.tracks

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    val tracks = ArrayList<Track>()
    private val gson = Gson()

    fun getHistory(): ArrayList<Track>{
        val history = sharedPreferences.getString(HISTORY_KEY, null) ?: return arrayListOf()

        val type = object : TypeToken<ArrayList<Track>>() {}.type

        return gson.fromJson(history, type)
    }

    companion object {
        const val HISTORY_KEY = "history_key"
    }
}