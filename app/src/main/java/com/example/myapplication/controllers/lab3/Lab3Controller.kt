package com.example.myapplication.controllers.lab3

import android.app.Application
import android.util.Log
import android.util.Pair
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.lab1.RegisterContents
import com.example.myapplication.model.lab1.RegisterRequest
import com.example.myapplication.model.lab1.SearchContents
import com.example.myapplication.model.lab1.SearchRequest
import com.example.myapplication.model.lab1.Staff
import com.example.myapplication.model.lab1.Staff.Companion.getStaffList
import com.example.myapplication.model.lab5.Good
import com.example.myapplication.network.lab1.RetrofitClient.Companion.get
import com.example.myapplication.network.lab1.api.APIService
import com.example.myapplication.repository.lab5.DAO.GoodsDAO
import com.example.myapplication.repository.lab5.DAO.GoodsDatabase
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executors

class Lab3Controller(application: Application) : AndroidViewModel(application) {

    private lateinit var apiService: APIService
    private lateinit var goodsDAO: GoodsDAO
    lateinit var dataUserID: MutableLiveData<Int>
    lateinit var dataToken: MutableLiveData<String>
    lateinit var dataGoodList: MutableLiveData<List<Good>>
    lateinit var dataStaffList: MutableLiveData<List<Staff>>
    lateinit var combinedData: MediatorLiveData<Pair<String, Int>>
    lateinit var combinedListData: MediatorLiveData<Pair<List<Staff>, List<Good>>>

    init {
        init(application)
    }

    private fun init(application: Application?) {
        apiService = get().create(APIService::class.java)
        val database = GoodsDatabase.getInstance(application)
        goodsDAO = database.goodsDAO
        dataUserID = MutableLiveData()
        dataToken = MutableLiveData()
        combinedData = MediatorLiveData()
        combinedListData = MediatorLiveData()
        dataGoodList = MutableLiveData()
        dataStaffList = MutableLiveData()

        /*Thằng này dùng để kết hợp 2 dữ liệu token và userID để thông báo 1 lần*/
        combinedData!!.addSource(
            dataToken!!
        ) { token: String ->
            val userID = dataUserID!!.value
            if (userID != null) {
                combinedData!!.value = Pair(
                    token, userID
                )
            }
        }
        combinedData!!.addSource(dataUserID!!) { userID: Int ->
            val token = dataToken!!.value
            if (token != null) {
                combinedData!!.value = Pair(
                    token, userID
                )
            }
        }

        /*Thằng này dùng để kết hợp 2 dữ liệu danh sách để thông báo 1 lần*/
        combinedListData!!.addSource(
            dataStaffList!!
        ) { staffList: List<Staff> ->
            val goodList = dataGoodList!!.value
            if (goodList != null) {
                combinedListData!!.value = Pair(
                    staffList, goodList
                )
            }
        }
        combinedListData!!.addSource(
            dataGoodList!!
        ) { goodList: List<Good> ->
            val staffList = dataStaffList!!.value
            if (staffList != null) {
                combinedListData!!.value = Pair(
                    staffList, goodList
                )
            }
        }
    }

    //Hàm này dùng để lấy danh sách sản phẩm
    fun getGoods() {
        //Check xem database có dữ liệu không
        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {
                val count = goodsDAO!!.count

                //Nếu có thì lấy dữ liệu từ database đưa lên view
                if (count > 0) {
                    Log.i("DEBUG", "Load du lieu tu database")
                    val goodList = goodsDAO!!.findAll()

                    //Sau khi lấy dữ liệu từ database tiến hành gán giá trị
                    dataGoodList!!.postValue(goodList)
                } else {

                    //Nếu không có dữ liệu tiến hành call api lấy dữ liệu
                    Log.i("DEBUG", "Call api lay du lieu")
                    //Khởi tạo body để gửi đi {"contents":{}}
                    val requestBody = JsonObject()
                    val requestContent = JsonObject()
                    requestBody.add("contents", requestContent)

                    //Tiến hành call api để lấy danh sách dữ liệu
                    apiService.getGoods(requestContent).enqueue(object : Callback<JsonElement?> {
                        override fun onResponse(
                            call: Call<JsonElement?>, response: Response<JsonElement?>
                        ) {
                            if (response.isSuccessful) {
                                val jsonElement = response.body()
                                if (jsonElement != null && jsonElement.isJsonObject) {
                                    val jsonObject = jsonElement.asJsonObject
                                    val contents = jsonObject.getAsJsonArray("contents")


                                    //Lấy good và gán dữ liệu vào mutable live data
                                    val goodsJsonArray =
                                        contents[0].asJsonObject["goods"].asJsonArray
                                    val goodListData = Good.getGoods(goodsJsonArray)
                                    dataGoodList!!.postValue(goodListData)

                                    //Thêm dữ liệu vào database
                                    addGoodToDatabase(goodListData)
                                } else {
                                    Log.e("DEBUG", "Invalid JSON response")
                                }
                            } else {
                                try {
                                    Log.e(
                                        "DEBUG",
                                        "Response error: " + response.errorBody()!!.string()
                                    )
                                } catch (e: IOException) {
                                    Log.e(
                                        "DEBUG", "Error reading error body: " + e.message
                                    )
                                }
                            }
                        }

                        override fun onFailure(
                            call: Call<JsonElement?>, t: Throwable
                        ) {
                            Log.e("DEBUG", "Error:$t")
                        }
                    })
                }
            } catch (er: Exception) {
                Log.i("DEBUG", "Error from get goods list$er")
            }
        }
    }


    //Thêm list sản phẩm vào database
    private fun addGoodToDatabase(goodListData: List<Good>) {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            try {
                for (good in goodListData) {
                    goodsDAO!!.add(good)
                }
            } catch (er: Exception) {
                Log.i("DEBUG", "Error from get goods list$er")
            }
        }
    }

    //Hàm này dùng để call api đăng ký thiết bị lấy token và userID
    fun registerDeviceApp(contents: RegisterContents?) {

        //Thằng này là dữ liệu gửi đi dưới body
        val request = RegisterRequest(contents!!)
        apiService!!.registerDeviceApp(request).enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                if (response.isSuccessful) {
                    val jsonElement = response.body()
                    if (jsonElement != null && jsonElement.isJsonObject) {
                        val jsonObject = jsonElement.asJsonObject
                        val status = jsonObject.getAsJsonObject("status")
                        if (status["code"].asInt != 200) {
                            Log.e("ERROR", "Loi status:")
                        } else {
                            val contents = jsonObject.getAsJsonObject("contents")
                            val userInfo = contents.getAsJsonObject("userInfo")
                            val userID = userInfo["id"].asInt
                            val token = userInfo["token"].asString

                            //Sau khi lấy được token và userID tiến hành gán giá trị
                            dataUserID!!.postValue(userID)
                            dataToken!!.postValue(token)
                        }
                    } else {
                        Log.e("DEBUG", "Invalid JSON response")
                    }
                } else {
                    try {
                        Log.e("DEBUG", "Response error: " + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        Log.e("DEBUG", "Error reading error body: " + e.message)
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                Log.e("DEBUG", "Call api failed: " + t.message)
            }
        })
    }

    //Hàm này dùng để call api lấy danh sách nhân viên
    fun search(contents: SearchContents?) {

        //Thằng này là dữ liệu gửi đi dưới body
        val request = SearchRequest(
            contents!!
        )
        apiService!!.search(request).enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                try {
                    if (response.isSuccessful) {
                        val jsonElement = response.body()
                        if (jsonElement != null && jsonElement.isJsonObject) {
                            val jsonObject = jsonElement.asJsonObject
                            val status = jsonObject.getAsJsonObject("status")
                            if (status["code"].asInt != 200) {
                                Log.e("ERROR", "Loi status:")
                            } else {
                                dataStaffList!!.postValue(getStaffList(jsonElement))
                            }
                        } else {
                            Log.e("DEBUG", "Invalid JSON response")
                        }
                    } else {
                        Log.e("DEBUG", "Response error: " + response.errorBody())
                    }
                } catch (er: Exception) {
                    Log.e("DEBUG", "Error from search lab6Controller:$er")
                }
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                Log.e("DEBUG", "Call api failed: " + t.message)
            }
        })
    }
}