package com.example.myapplication

import com.example.myapplication.request.SearchRequest
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {

    @POST("registerDeviceApp/")
    fun registerDeviceApp(
        @Body data: RegisterRequest
    ): Call<JsonElement>


    @POST("searchSupMCPApp/")
    fun search(
        @Body data: SearchRequest
    ): Call<JsonElement>

}