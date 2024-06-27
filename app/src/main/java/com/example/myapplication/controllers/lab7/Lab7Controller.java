package com.example.myapplication.controllers.lab7;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.model.lab1.RegisterRequest;
import com.example.myapplication.model.lab5.Good;
import com.example.myapplication.model.lab5.GoodsCategory;
import com.example.myapplication.model.lab5.Unit;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.network.lab1.api.APIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lab7Controller extends ViewModel {

    private APIService apiService=RetrofitClient.Companion.get().create(APIService.class);

    public MutableLiveData<Integer> dataUserID;
    public MutableLiveData<String> dataToken;
    public MutableLiveData<String> dataError;

    public MediatorLiveData<Pair<String, Integer>> combinedData;


    public void init() {
        apiService = RetrofitClient.Companion.get().create(APIService.class);
        dataUserID = new MutableLiveData<>();
        dataToken = new MutableLiveData<>();
        dataError=new MutableLiveData<>();
        combinedData = new MediatorLiveData<>();

        combinedData.addSource(dataToken, token -> {
            Integer userID = dataUserID.getValue();
            if (userID != null) {
                combinedData.setValue(new Pair<>(token, userID));
            }
        });
    }

    public void registerDeviceApp(RegisterContents contents) {
        RegisterRequest request = new RegisterRequest(contents);
        apiService.registerDeviceApp(request).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();
                    Log.d("DEBUG", "body:" + jsonElement);
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonObject status = jsonObject.getAsJsonObject("status");
                        if (status.get("code").getAsInt() != 200) {
                            Log.e("DEBUG", "Loi status:");
                            dataError.postValue(status.get("message").getAsString());
                            dataUserID.postValue(-1);
                            dataToken.postValue("");

                        } else {
                            JsonObject contents = jsonObject.getAsJsonObject("contents");
                            JsonObject userInfo = contents.getAsJsonObject("userInfo");
                            int userID = userInfo.get("id").getAsInt();
                            String token = userInfo.get("token").getAsString();

                            /*Log.i("DEBUG", "User id lay duoc:" + userID);
                            Log.i("DEBUG", "Token lay duoc:" + token);*/

                            dataUserID.postValue(userID);
                            dataToken.postValue(token);
                        }
                    } else {
                        Log.e("DEBUG", "Invalid JSON response");
                    }
                } else {
                    try {
                        Log.e("DEBUG", "Response error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("DEBUG", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("DEBUG", "Call api failed: " + t.getMessage());
            }
        });
    }


}
