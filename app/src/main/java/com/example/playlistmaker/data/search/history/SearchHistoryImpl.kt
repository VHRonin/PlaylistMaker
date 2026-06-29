package com.example.playlistmaker.data.search.history

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistory {
    private val tracks = ArrayList<TrackDto>()
    private val gson = Gson()

    override fun getHistory(): ArrayList<TrackDto>{
        val history = sharedPreferences.getString(HISTORY_KEY, null) ?: return arrayListOf()

        val type = object : TypeToken<ArrayList<TrackDto>>() {}.type

        return gson.fromJson(history, type)
    }

    override fun addTrackToHistory(track: TrackDto, onHistoryClick: () -> Unit){
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

    override fun clearHistory(){
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()

        tracks.clear()
    }

    override fun fillTracksHistory(){
        tracks.clear()
        tracks.addAll(getHistory())
    }

    override fun getTracks(): ArrayList<TrackDto> = tracks

    companion object {
        const val HISTORY_KEY = "history_key"
        const val MAX_SIZE = 10
    }
}