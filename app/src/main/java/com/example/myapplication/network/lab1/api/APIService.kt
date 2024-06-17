package com.example.myapplication.network.lab1.api

import com.example.myapplication.model.lab1.RegisterRequest
import com.example.myapplication.model.lab1.SearchRequest
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {

    @POST("registerDeviceApp/")
    fun registerDeviceApp(
        @Body data: RegisterRequest
    ): Call<JsonElement>


    @POST("searchSupMCPApp/")
    fun search(
        @Body data: SearchRequest
    ): Call<JsonElement>

    @POST("getGoodsSalesmanApp/")
    fun getGoods(
        @Body data:JsonObject
    ): Call<JsonElement>


}