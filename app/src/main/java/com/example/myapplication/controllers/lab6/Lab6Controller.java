package com.example.myapplication.controllers.lab6;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.model.lab1.RegisterRequest;
import com.example.myapplication.model.lab1.SearchContents;
import com.example.myapplication.model.lab1.Staff;
import com.example.myapplication.model.lab5.Good;
import com.example.myapplication.model.lab6.Distributor;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.network.lab1.api.APIService;
import com.example.myapplication.repository.lab5.DAO.GoodsDAO;
import com.example.myapplication.repository.lab5.DAO.GoodsDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.model.lab1.SearchRequest;


public class Lab6Controller extends AndroidViewModel {

    public MutableLiveData<Integer> dataUserID;
    public MutableLiveData<String> dataToken;
    public MutableLiveData<List<Good>> dataGoodList;
    public MutableLiveData<List<Distributor>> dataDistributorList;
    public MutableLiveData<List<Staff>> dataStaffList;
    public MediatorLiveData<Pair<String, Integer>> combinedData;
    public MediatorLiveData<Pair<List<Distributor>, List<Good>>> combinedListData;
    private APIService apiService;
    private GoodsDAO goodsDAO;

    public Lab6Controller(@NonNull Application application) {
        super(application);
        init(application);
    }

    public void init(Application application) {

        apiService = RetrofitClient.Companion.get().create(APIService.class);
        GoodsDatabase database = GoodsDatabase.getInstance(application);
        goodsDAO = database.getGoodsDAO();
        dataUserID = new MutableLiveData<>();
        dataToken = new MutableLiveData<>();
        combinedData = new MediatorLiveData<>();
        combinedListData = new MediatorLiveData<>();
        dataGoodList = new MutableLiveData<>();
        dataStaffList = new MutableLiveData<>();
        dataDistributorList = new MutableLiveData<>();

        /*Thằng này dùng để kết hợp 2 dữ liệu token và userID để thông báo 1 lần*/
        combinedData.addSource(dataToken, token -> {
            Integer userID = dataUserID.getValue();
            if (userID != null) {
                combinedData.setValue(new Pair<>(token, userID));
            }
        });

        /*Thằng này dùng để kết hợp 2 dữ liệu danh sách để thông báo 1 lần*/
        combinedListData.addSource(dataDistributorList, distributorList -> {
            List<Good> goodList = dataGoodList.getValue();
            if (goodList != null) {
                combinedListData.setValue(new Pair<>(distributorList, goodList));
            }
        });
    }

    //Hàm này dùng để lấy danh sách sản phẩm
    public void getGoods() {

        //Check xem database có dữ liệu không
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                int count = goodsDAO.getCount();
//
//                //Nếu có thì lấy dữ liệu từ database đưa lên view
//                if(count>0){
//                    Log.i("DEBUG","Load du lieu tu database");
//                    List<Good> goodList=goodsDAO.findAll();
//
//                    //Sau khi lấy dữ liệu từ database tiến hành gán giá trị
//                    dataGoodList.postValue(goodList);
//                }else{

                //Nếu không có dữ liệu tiến hành call api lấy dữ liệu
                Log.i("DEBUG", "Call api lay du lieu");
                //Khởi tạo body để gửi đi {"contents":{}}
                JsonObject requestBody = new JsonObject();
                JsonObject requestContent = new JsonObject();
                requestBody.add("contents", requestContent);

                //Tiến hành call api để lấy danh sách dữ liệu
                apiService.getGoods(requestContent).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            JsonElement jsonElement = response.body();
                            if (jsonElement != null && jsonElement.isJsonObject()) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JsonArray contents = jsonObject.getAsJsonArray("contents");

                                //Lấy good và gán dữ liệu vào mutable live data
                                JsonArray goodsJsonArray = contents.get(0).getAsJsonObject().get("goods").getAsJsonArray();
                                List<Good> goodListData = Good.getGoods(goodsJsonArray);
                                dataGoodList.postValue(goodListData);

                                //Thêm dữ liệu vào database
                                if (count == 0) {
                                    addGoodToDatabase(goodListData);
                                }

                                //Lấy danh sách npp
                                JsonArray distributorsJsonArray = contents.get(0).getAsJsonObject().get("nppList").getAsJsonArray();
                                List<Distributor> distributorList = Distributor.getDistributorList(distributorsJsonArray);
                                dataDistributorList.postValue(distributorList);

                                for (Distributor distributor : distributorList) {
                                    Log.i("DEBUG", "Du lieu:" + distributor.getName());
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
                        Log.e("DEBUG", "Error:" + t);
                    }
                });
//                }
            } catch (Exception er) {
                Log.i("DEBUG", "Error from get goods list" + er);

            }
        });
    }

    //Thêm list sản phẩm vào database
    private void addGoodToDatabase(List<Good> goodListData) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                for (Good good : goodListData) {
                    goodsDAO.add(good);
                }
            } catch (Exception er) {
                Log.i("DEBUG", "Error from get goods list" + er);

            }
        });
    }

    //Hàm này dùng để call api đăng ký thiết bị lấy token và userID
    public void registerDeviceApp(RegisterContents contents) {

        //Thằng này là dữ liệu gửi đi dưới body
        RegisterRequest request = new RegisterRequest(contents);

        apiService.registerDeviceApp(request).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();
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

                            //Sau khi lấy được token và userID tiến hành gán giá trị
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


    //Hàm này dùng để call api lấy danh sách nhân viên
    public void search(SearchContents contents) {

        //Thằng này là dữ liệu gửi đi dưới body
        SearchRequest request = new SearchRequest(contents);

        apiService.search(request).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    if (response.isSuccessful()) {
                        JsonElement jsonElement = response.body();
                        if (jsonElement != null && jsonElement.isJsonObject()) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonObject status = jsonObject.getAsJsonObject("status");
                            if (status.get("code").getAsInt() != 200) {
                                Log.e("ERROR", "Loi status:");
                            } else {
                                dataStaffList.postValue(Staff.Companion.getStaffList(jsonElement));
                            }
                        } else {
                            Log.e("DEBUG", "Invalid JSON response");
                        }
                    } else {
                        Log.e("DEBUG", "Response error: " + response.errorBody());
                    }
                } catch (Exception er) {
                    Log.e("DEBUG", "Error from search lab6Controller:" + er);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e("DEBUG", "Call api failed: " + t.getMessage());
            }
        });
    }

}

