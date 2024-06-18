package com.example.myapplication.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.lab4.Lab4ItemAdapter;
import com.example.myapplication.adapter.lab4.Lab4Listener;
import com.example.myapplication.controllers.lab1.Controller;
import com.example.myapplication.databinding.Lab2LayoutBinding;
import com.example.myapplication.model.lab1.RegisterContents;
import com.example.myapplication.model.lab1.SearchContents;
import com.example.myapplication.model.lab1.Staff;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;
import com.example.myapplication.network.lab1.RetrofitClient;
import com.example.myapplication.utils.lab2.CustomDialog;
import com.example.myapplication.utils.lab2.CustomDialogJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lab4Activity extends ComponentActivity {

    private Lab2LayoutBinding binding;

    private Lab4ItemAdapter adapter;

    private Controller mViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = Lab2LayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo viewmodel
        mViewModel = new ViewModelProvider(this).get(Controller.class);

        //Đăng ký branch
        RetrofitClient.Companion.setBranch("1");

        if (mViewModel.getDataToken() != null) {
            //Call API đăng ký thiết bị
            mViewModel.registerDeviceApp(new RegisterContents(
                    "test2@tmmn",
                    "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                    "22184a05bdd68cb1",
                    "123456",
                    "1.5.7",
                    "tuhDevice"));
        }

        //Kiểm tra xem dữ liệu sau khi call api có hoạt động không
        observeDataToken();

        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        observeStaffList();
    }

    //Hàm này dùng để quan sát giá trị của token và userID có lấy được hay chưa
    private void observeDataToken() {
        mViewModel.getCombinedData().observe(this, data -> {
            if (mViewModel.getDataToken().getValue() != null) {

                //Set token cho client
                RetrofitClient.Companion.setAuthToken(data.getFirst());

                //Tiến hành call api để lấy dữ liệu danh sách nhân viên
                SearchContents searchContents = new SearchContents(-1, 755);
                mViewModel.search(searchContents);
            }
        });
    }

    private void btnConfirmHandle() {
        binding.btnConfirm.setOnClickListener(view -> {

            //Lấy danh sách sản phẩm từ dapter
            List<Product> productList = adapter.getValues();

            /*if (hasDuplicateProductName(productList)) {
                Toast.makeText(Lab4Activity.this, "San pham bi trung vui long nhap lai", Toast.LENGTH_SHORT).show();
            } else if (productHasEmptyAmount(productList)) {
                Toast.makeText(Lab4Activity.this, "Khong duoc de trong gia tri", Toast.LENGTH_SHORT).show();
            } else {
                CustomDialogJava dialog = new CustomDialogJava(Lab4Activity.this, productList);
                dialog.show();
            }*/
            CustomDialogJava dialog = new CustomDialogJava(Lab4Activity.this, productList);
            dialog.show();
        });
    }

    private void setUpAdapter(List<Item> itemAdapterList, List<Staff> staffList) {
        adapter = new Lab4ItemAdapter(itemAdapterList, new Lab4Listener() {

            //Hàm này dùng để xử lý khi thêm sản phẩm
            @Override
            public void add() {

                List<ProductType> productTypeList = new ArrayList<>();
                Collections.addAll(productTypeList,
                        new ProductType("Type 1", 0),
                        new ProductType("Type 2", 0),
                        new ProductType("Type 3", 0)
                );

                List<String> productList1;
                productList1 = staffList.stream()
                        .map(Staff::getName)
                        .collect(Collectors.toCollection(ArrayList::new));

                adapter.add(new Item(productList1, productTypeList));
            }

            //Hàm này dùng để xử lý khi xóa sản phẩm
            @Override
            public void delete(Item item) {
                adapter.delete(item);
            }
        });
        binding.recyclerView.setAdapter(adapter);
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


    //Hàm này dùng để quan sát giá trị danh sách nhân viên có dữ liệu thay đổi để cập nhật UI
    private void observeStaffList() {
        mViewModel.getDataStaffList().observe(this, staffList -> {
            try {
                if (staffList.isEmpty()) {
                    Toast.makeText(Lab4Activity.this, "Không tìm thấy dữ liệu staff list", Toast.LENGTH_SHORT).show();
                } else {

                    //Tiến hành đổ dữ liệu vào spinner
                    List<Item> itemAdapterList = new ArrayList<>();

                    List<ProductType> productTypeList = new ArrayList<>();
                    Collections.addAll(productTypeList,
                            new ProductType("Type 1", 0),
                            new ProductType("Type 2", 0),
                            new ProductType("Type 3", 0)
                    );

                    //Gán dữ liệu vào danh sách sản phẩm
                    List<String> productList1;
                    productList1 = staffList.stream()
                            .map(Staff::getName)
                            .collect(Collectors.toCollection(ArrayList::new));
                    itemAdapterList.add(new Item(productList1, productTypeList));

                    //Tạo adapter cho recyclerView khi đã có dữ liệu
                    setUpAdapter(itemAdapterList, staffList);

                    //Tạo dialog
                    btnConfirmHandle();

                }
            } catch (Exception er) {
                Log.i("DEBUG", "Error from observe staff list:" + er);
            }
        });
    }

}
