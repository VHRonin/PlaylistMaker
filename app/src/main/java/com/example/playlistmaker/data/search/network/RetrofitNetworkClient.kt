package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TracksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val iTunesService: ITunesApi) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TracksRequest){
            withContext(Dispatchers.IO){
                try {
                    val resp = iTunesService.search(dto.term)

                    resp.apply { resultCode = 200 }
                } catch (e: Exception){
                    Response().apply { resultCode = 500 }
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}