package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksRequest){
            try {
                val resp = iTunesService.search(dto.term).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            }
            catch (e: Exception){
                return Response().apply { resultCode = -1 }
            }
        }
        else {
            return Response().apply { resultCode = 400 }
        }
    }
}