package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.lab4.Lab4ItemAdapter;
import com.example.myapplication.adapter.lab4.Lab4Listener;
import com.example.myapplication.adapter.lab6.Lab6ItemAdapter;
import com.example.myapplication.adapter.lab6.Lab6Listener;
import com.example.myapplication.controllers.lab1.Controller;
import com.example.myapplication.controllers.lab6.Lab6Controller;
import com.example.myapplication.controllers.lab6.Lab6ControllerFactory;
import com.example.myapplication.databinding.Lab6LayoutBinding;
import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.model.lab1.SearchContents;
import com.example.myapplication.model.lab1.Staff;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;
import com.example.myapplication.model.lab5.Good;
import com.example.myapplication.model.lab6.Item6;
import com.example.myapplication.model.lab6.Product6;
import com.example.myapplication.model.lab6.ProductType6;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.repository.lab5.DAO.GoodsDAO;
import com.example.myapplication.repository.lab5.DAO.GoodsDatabase;
import com.example.myapplication.utils.lab2.CustomDialogJava;
import com.example.myapplication.utils.lab6.CustomDialogJava6;
import com.example.myapplication.utils.lab6.FinalCustomDialog;
import com.example.myapplication.utils.lab6.LoadingDialog;
import com.example.myapplication.utils.lab6.MarginItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lab6Activity extends AppCompatActivity {

    private Lab6LayoutBinding binding;

    private Lab6ItemAdapter adapter;

    private Lab6Controller mViewModel;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = Lab6LayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo loading
        loadingDialog = new LoadingDialog(this);

        //Khởi tạo viewmodel
        Lab6ControllerFactory factory = new Lab6ControllerFactory(getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(Lab6Controller.class);

        observeData();

        btnConfirmHandle();

        setSupportActionBar(binding.topAppBar);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        binding.topAppBar.setNavigationOnClickListener(view -> finish());


        //Gọi hàm lấy danh sách nhân viên
        getData();

    }


    //Hàm này dùng để clear focus tất cả edit text khi nhấn ra ngoài màn hình
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void btnConfirmHandle() {
        binding.btnConfirm.setOnClickListener(view -> {

            //Lấy danh sách sản phẩm từ dapter
            List<Product6> productList = adapter.getValues();

            /*if (hasDuplicateProductName(productList)) {
                Toast.makeText(Lab4Activity.this, "San pham bi trung vui long nhap lai", Toast.LENGTH_SHORT).show();
            } else if (productHasEmptyAmount(productList)) {
                Toast.makeText(Lab4Activity.this, "Khong duoc de trong gia tri", Toast.LENGTH_SHORT).show();
            } else {
                CustomDialogJava dialog = new CustomDialogJava(Lab4Activity.this, productList);
                dialog.show();
            }*/
            /*CustomDialogJava6 dialog = new CustomDialogJava6(Lab6Activity.this, productList);
            dialog.show();*/

            //Hiển thị dialog
            FinalCustomDialog dialog=new FinalCustomDialog(Lab6Activity.this,productList);
            dialog.show();
        });
    }

    //Hàm này dùng để khởi tạo adapter cho view
    private void setUpAdapter(List<Item6> itemList, List<String> productList,List<ProductType6> productTypeList) {
        adapter = new Lab6ItemAdapter(itemList, new Lab6Listener() {

            //Hàm này dùng để xử lý khi thêm sản phẩm
            @Override
            public void add() {

                //Tạo mới 2 danh sách sản phẩm và loại sản phẩm tránh trường hợp sử dụng chung
                List<String> addProductList=new ArrayList<>();
                for(String value:productList){
                    addProductList.add(value);
                }

                List<ProductType6> addProductTypeList=new ArrayList<>();
                for(ProductType6 productType6:productTypeList){
                    addProductTypeList.add(new ProductType6(productType6.getName(),new ArrayList<>(Arrays.asList(0L, 0L))));
                }

                //Gọi hàm add để thêm item vào adapter
                adapter.add(new Item6(addProductList,addProductTypeList));
            }

            //Hàm này dùng để xử lý khi xóa sản phẩm
            @Override
            public void delete(Item6 item) {
                adapter.delete(item);
            }
        });

        //Tiến hành show recyclerView
        new Handler().postDelayed(() -> {
            loadingDialog.setDialog(false);
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.recyclerView.setAdapter(adapter);
        }, 1000);
    }

    public boolean hasDuplicateProductName(List<Product> productList) {
        Set<String> seenNames = new HashSet<>();
        for (Product product : productList) {
            if (!seenNames.add(product.getProductName())) {
                return true;
            }
        }
        return false;
    }

    private boolean productHasEmptyAmount(List<Product> productList) {
        for (Product product : productList) {
            for (ProductType productType : product.getListProductType()) {
                if (productType.getAmount() == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    //Hàm này dùng để lấy danh sách nhân viên và sản phẩm
    private void getData() {
        loadingDialog.setDialog(true);
        SearchContents searchContents = new SearchContents(-1, 755);

        //Call API lây danh sách nhân viên
        mViewModel.search(searchContents);

        //Call API lấy danh sách sản phẩm
        mViewModel.getGoods();
    }

    //Hàm này dùng để quan sát dữ liệu 2 danh sách nhân viên và sản phẩm
    private void observeData() {
        mViewModel.combinedListData.observe(this, data -> {
            try{
                if(data.first!=null&&data.first.isEmpty()){
                    Toast.makeText(this,"Danh sách nhân viên rỗng",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.second!=null&&data.second.isEmpty()){
                    Toast.makeText(this,"Danh sách sản phẩm rỗng",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Khởi tạo list item cho adapter
                List<Item6> itemList=new ArrayList<>();

                //Lấy danh sách sản phẩm
                List<Good> goodList=data.second;

                //Khởi tạo danh sách loại sản phẩm bao gồm tên và import,export(Giá trị ban đầu là 0,0)
                List<ProductType6> productTypeAdapterList=new ArrayList<>();
                for(Good good:goodList){
                    productTypeAdapterList.add(new ProductType6(good.getName(),new ArrayList<>(Arrays.asList(0L, 0L))));
                }

                //Khởi tạo danh sách sản phẩm
                List<String> productList=new ArrayList<>();
                for(Staff staff:data.first){
                    productList.add(staff.getName());
                }
                /*productList = data.first.stream()
                        .map(Staff::getName)
                        .collect(Collectors.toCollection(ArrayList::new));*/

                //Khởi tạo giá trị ban đầu cho adapter
                itemList.add(new Item6(productList,productTypeAdapterList));

                //Khởi tạo adapter
                /*binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));*/
                int marginBottom = (int) (5 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
                binding.recyclerView.addItemDecoration(new MarginItemDecoration(marginBottom));

                setUpAdapter(itemList, productList,productTypeAdapterList);

            }catch (Exception er){
                Log.e("DEBUG","Error from observeData:"+er);
            }
        });
    }



}

