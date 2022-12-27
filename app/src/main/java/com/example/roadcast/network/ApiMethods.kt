package com.example.roadcast.network

import com.example.roadcast.network.APIEndPoints.Companion.ENTRIES
import com.example.roadcast.ui.main.BaseResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiMethods {

    @GET(ENTRIES)
    suspend fun getEntries(): Response<BaseResponse>

}