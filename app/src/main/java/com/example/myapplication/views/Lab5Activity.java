package com.example.myapplication.views;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.lab4.Lab4ItemAdapter;
import com.example.myapplication.adapter.lab4.Lab4Listener;
import com.example.myapplication.controllers.lab1.Controller;
import com.example.myapplication.controllers.lab5.Lab5Controller;
import com.example.myapplication.databinding.Lab2LayoutBinding;
import com.example.myapplication.databinding.Lab5LayoutBinding;
import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.model.lab1.SearchContents;
import com.example.myapplication.model.lab1.Staff;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;
import com.example.myapplication.model.lab5.Good;
import com.example.myapplication.model.lab5.GoodsCategory;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.repository.lab5.DAO.GoodsCategoryDAO;
import com.example.myapplication.repository.lab5.DAO.GoodsDAO;
import com.example.myapplication.repository.lab5.DAO.GoodsDatabase;
import com.example.myapplication.utils.lab2.CustomDialogJava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Lab5Activity extends ComponentActivity {

    private Lab5LayoutBinding binding;

    private Lab5Controller mViewModel;
    private GoodsDatabase database;
    private GoodsCategoryDAO goodsCategoryDAO;
    private GoodsDAO goodsDAO;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = Lab5LayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo viewmodel
        mViewModel = new ViewModelProvider(this).get(Lab5Controller.class);
        mViewModel.init();

        //Khởi tạo database
        database = GoodsDatabase.getInstance(this);
        goodsCategoryDAO = database.getGoodsCategoryDAO();
        goodsDAO = database.getGoodsDAO();

        //Đăng ký branch
        RetrofitClient.Companion.setBranch("1");

        if (mViewModel.dataToken.getValue() == null) {
            //Call API đăng ký thiết bị
            mViewModel.registerDeviceApp(new RegisterContents("test2@tmmn", "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", "22184a05bdd68cb1", "123456", "1.5.7", "tuhDevice"));
        }

        //Kiểm tra xem dữ liệu sau khi call api có hoạt động không
        observeDataToken();
        observeGoodsCategory();
        observeGoodsList();

        btnCallHandle();
        btnDeleteHandle();

    }

    //Hàm này dùng để quan sát giá trị của token và userID có lấy được hay chưa
    private void observeDataToken() {
        mViewModel.combinedData.observe(this, data -> {
            if (mViewModel.dataToken.getValue() != null) {

                //Set token cho client
                RetrofitClient.Companion.setAuthToken(data.first);

                //Tiến hành call api để lấy dữ liệu danh sách sản phẩm
                mViewModel.getGoods();
            }
        });
    }

    //Hàm dùng để call api lấy danh sách sản phẩm
    private void btnCallHandle() {
        binding.btnCall.setOnClickListener(view -> {

            //Call api lấy danh sách sản phẩm
            mViewModel.getGoods();

        });
    }

    //Hàm dùng để xóa dữ liệu sản phẩm khỏi database và cập nhật UI
    private void btnDeleteHandle(){
        binding.btnDelete.setOnClickListener(view -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(getMainLooper());
            executorService.execute(() -> {
                goodsDAO.deleteAll();
                int count=goodsDAO.getCount();

                handler.post(() -> {
                    //Xóa thành công
                    if(count==0){
                        Toast.makeText(this, "Xóa sản phẩm khỏi database thành công", Toast.LENGTH_SHORT).show();

                        //Tiến hành cập nhật lại UI set empty cho list
                        List<String> emptyList = new ArrayList<>();
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList);
                        binding.goodsList.setAdapter(adapter);
                    }else{
                        Log.i("DEBUG","Error from delete good handle");
                    }
                });
            });
        });
    }

    private void setUpGoodsCategory(GoodsCategory goodsCategory) {
        binding.goodsCategory.setText("ID:" + goodsCategory.getId() + " Tên:" + goodsCategory.getName());
    }

    //Hàm dùng để thêm goods category
    private void addGoodsCategory() {
        //Luôn phải thực hiện trong background
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            try {
                goodsCategoryDAO.add(mViewModel.goodsCategory.getValue());

                handler.post(() -> {
                    Toast.makeText(this, "Thêm vào database thành công", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception er) {
                Log.i("DEBUG", "Error from add goods category" + er);
            }
        });


    }

    //Hàm dùng để lấy danh sách loại sản phẩm
    private void getGoodsCategoryList() {
        //Luôn phải thực hiện trong background
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                List<GoodsCategory> goodsCategoryList = goodsCategoryDAO.findAll();
                for (GoodsCategory goodsCategory : goodsCategoryList) {
                    Log.i("DEBUG", "ID:" + goodsCategory.getId() + " name:" + goodsCategory.getName());
                }
            } catch (Exception er) {
                Log.i("DEBUG", "Error from get goods category list" + er);

            }
        });
    }


    //Hàm này dùng để quan sát giá trị goodsCategory thay đổi
    private void observeGoodsCategory() {
        mViewModel.goodsCategory.observe(this, goodsCategory -> {
            if (goodsCategory == null) {
                Toast.makeText(Lab5Activity.this, "Không tìm thấy dữ liệu goods category", Toast.LENGTH_SHORT).show();
            } else {

                //Hiển thị goods Category lên view
                setUpGoodsCategory(goodsCategory);
            }
        });
    }


    //Hàm này dùng để quan sát giá trị của danh sách sản phẩm
    private void observeGoodsList() {
        mViewModel.goodList.observe(this, goodList -> {
            if (goodList == null || goodList.isEmpty()) {
                Toast.makeText(Lab5Activity.this, "Không tìm thấy dữ liệu goods", Toast.LENGTH_SHORT).show();
            } else {

                //Tiến hành kiểm tra xem database có dữ liệu chưa
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(getMainLooper());
                executorService.execute(() -> {
                    try {
                        int count = goodsDAO.getCount();
                        List<String> goodListString = new ArrayList<>();

                        //Không có dữ liệu trong database tiến hành insert data
                        if (count == 0) {
                            for (Good good : goodList) {
                                goodsDAO.add(good);//Thêm vào database
                            }

                            //Lấy dữ liệu từ database
                            List<Good> goodListDatabase=goodsDAO.findAll();
                            for(Good good :goodListDatabase){
                                goodListString.add("ID:" + good.getId() + " Tên:" + good.getName() + " Giá:" + good.getPrice());//Dùng để chuyển good về string đưa lên view
                            }

                            handler.post(() -> {
                                Toast.makeText(this, "Thêm dữ liệu sản phẩm vào database thành công", Toast.LENGTH_SHORT).show();
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goodListString);
                                binding.goodsList.setAdapter(adapter);
                            });
                        } else { //Đã có dữ liệu thì tiến hành cập nhật

                            for (Good good : goodList) {
                                goodsDAO.update(good);//Cập nhật dữ liệu
                            }

                            //Lấy dữ liệu từ database
                            List<Good> goodListDatabase=goodsDAO.findAll();
                            for(Good good :goodListDatabase){
                                goodListString.add("ID:" + good.getId() + " Tên:" + good.getName() + " Giá:" + good.getPrice());//Dùng để chuyển good về string đưa lên view
                            }

                            handler.post(() -> {
                                Toast.makeText(this, "Cập nhật dữ liệu sản phẩm vào database thành công", Toast.LENGTH_SHORT).show();
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, goodListString);
                                binding.goodsList.setAdapter(adapter);
                            });
                        }
                    } catch (Exception er) {
                        Log.i("DEBUG", "Error from observe good list" + er);

                    }
                });

            }
        });
    }

}
