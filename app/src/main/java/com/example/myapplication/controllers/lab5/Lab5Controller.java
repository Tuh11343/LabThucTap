package com.example.myapplication.controllers.lab5;

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
import com.example.myapplication.network.lab1.api.APIService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.google.gson.JsonObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lab5Controller extends ViewModel {

    private APIService apiService=RetrofitClient.Companion.get().create(APIService.class);

    public MutableLiveData<Integer> dataUserID;
    public MutableLiveData<String> dataToken;
    public MutableLiveData<GoodsCategory> goodsCategory;
    public MutableLiveData<List<Good>> goodList;

    public MutableLiveData<List<List<Unit>>> unitList;

    public MediatorLiveData<Pair<String, Integer>> combinedData;


    public void init() {
        apiService = RetrofitClient.Companion.get().create(APIService.class);
        dataUserID = new MutableLiveData<>();
        dataToken = new MutableLiveData<>();
        goodsCategory = new MutableLiveData<>();
        combinedData = new MediatorLiveData<>();
        goodList=new MutableLiveData<>();
        unitList=new MutableLiveData<>(new ArrayList<>());

        combinedData.addSource(dataToken, token -> {
            Integer userID = dataUserID.getValue();
            if (userID != null) {
                combinedData.setValue(new Pair<>(token, userID));
            }
        });

        combinedData.addSource(dataUserID, userID -> {
            String token = dataToken.getValue();
            if (token != null) {
                combinedData.setValue(new Pair<>(token, userID));
            }
        });
    }

    public void getGoods(){

        //Khởi tạo body để gửi đi {"contents":{}}
        JsonObject requestBody = new JsonObject();
        JsonObject requestContent = new JsonObject();
        requestBody.add("contents", requestContent);

        //Tiến hành call api để lấy danh sách dữ liệu
        apiService.getGoods(requestContent).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    JsonElement jsonElement = response.body();
//                    Log.d("DEBUG", "body:" + jsonElement);
                    if(jsonElement!=null&&jsonElement.isJsonObject()){
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonArray contents = jsonObject.getAsJsonArray("contents");

                        //Lấy goodsCategory và gán dữ liệu vào mutable live data
                        JsonObject goodsCategoryObject=contents.get(0).getAsJsonObject().get("goodsCategory").getAsJsonObject();
                        GoodsCategory goodsCategoryData=GoodsCategory.getGoodsCategory(goodsCategoryObject);
                        goodsCategory.postValue(goodsCategoryData);

                        //Lấy good và gán dữ liệu vào mutable live data
                        JsonArray goodsJsonArray=contents.get(0).getAsJsonObject().get("goods").getAsJsonArray();
                        List<Good> goodListData=Good.getGoods(goodsJsonArray);
                        goodList.postValue(goodListData);

                        //Lấy good unit và gán dữ liệu vào mutable live data
                        for (int i = 0; i < goodsJsonArray.size(); i++) {
                            JsonElement unitJsonElement = goodsJsonArray.get(i);
                            JsonObject unitJsonObject = unitJsonElement.getAsJsonObject();
                            JsonArray unitJsonArray = unitJsonObject.get("units").getAsJsonArray();
                            unitList.getValue().add(Unit.getUnits(goodListData.get(i).getId(),unitJsonArray));
                        }

                    }else{
                        Log.e("DEBUG", "Invalid JSON response");
                    }
                }else{
                    try {
                        Log.e("DEBUG", "Response error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("DEBUG", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("DEBUG", "Error:"+t);
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
//                    Log.d("DEBUG", "body:" + jsonElement);
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonObject status = jsonObject.getAsJsonObject("status");
                        if (status.get("code").getAsInt() != 200) {
                            Log.e("ERROR", "Loi status:");
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
