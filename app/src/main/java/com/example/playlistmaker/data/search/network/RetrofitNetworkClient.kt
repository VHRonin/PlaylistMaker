package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val iTunesService: ITunesApi) : NetworkClient {
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