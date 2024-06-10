package com.example.myapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.request.SearchContents
import com.example.myapplication.request.SearchRequest
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Controller: ViewModel() {

    var dataUserID=MutableLiveData<Int?>(null)
    var dataToken=MutableLiveData<String?>(null)
    var dataStaffList=MutableLiveData<MutableList<Staff>>()

    val apiService: APIService = RetrofitClient.get()!!.create(APIService::class.java)

    fun search(contents:SearchContents){
        val request=SearchRequest(contents)
        apiService.search(request).enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.i("DEBUG","Json:${response.body()}")
                if (response.isSuccessful) {
                    val jsonElement = response.body()
                    Log.d("DEBUG","body:$jsonElement")
                    if (jsonElement != null && jsonElement.isJsonObject) {
                        val jsonObject = jsonElement.asJsonObject
                        val status = jsonObject.getAsJsonObject("status")
                        if(status.get("code").asInt!=200){
                            Log.e("ERROR","Loi status:");
                        }else{
                            dataStaffList.postValue(Staff.getStaffList(jsonElement))
                        }

                    } else {
                        Log.e("DEBUG", "Invalid JSON response")
                    }
                } else {
                    Log.e("DEBUG", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e("DEBUG", "Call api failed: ${t.message}")
            }
        })
    }

    fun registerDeviceApp(contents:RegisterContents){
        val request=RegisterRequest(contents)
        apiService.registerDeviceApp(request).enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    val jsonElement = response.body()
                    Log.d("DEBUG","body:$jsonElement")
                    if (jsonElement != null && jsonElement.isJsonObject) {
                        val jsonObject = jsonElement.asJsonObject
                        val status = jsonObject.getAsJsonObject("status")
                        if(status.get("code").asInt!=200){
                            Log.e("ERROR","Loi status:");
                        }else{
                            val contents = jsonObject.getAsJsonObject("contents")
                            val userInfo = contents.getAsJsonObject("userInfo")
                            val userID = userInfo.get("id").asInt
                            val token=userInfo.get("token").asString

                            Log.i("DEBUG","User id lay duoc:${userID}")
                            Log.i("DEBUG","Token lay duoc:${token}")

                            dataUserID.postValue(userID)
                            dataToken.postValue(token)
                        }

                    } else {
                        Log.e("DEBUG", "Invalid JSON response")
                    }
                } else {
                    Log.e("DEBUG", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e("DEBUG", "Call api failed: ${t.message}")
            }
        })
    }
}