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

    fun addTrackToHistory(track: Track, onHistoryClick: () -> Unit = {}){
        fillTracksHistory()

        tracks.removeAll {it.trackId == track.trackId}

        tracks.add(0, track)

        if (tracks.size > MAX_SIZE) tracks.removeAt(tracks.size - 1)

        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()

        onHistoryClick()
    }

    fun clear(){
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()

        tracks.clear()
    }

    fun fillTracksHistory(){
        tracks.clear()
        tracks.addAll(getHistory())
    }

    companion object {
        const val HISTORY_KEY = "history_key"
        const val MAX_SIZE = 10
    }
}