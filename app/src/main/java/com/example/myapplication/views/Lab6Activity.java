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
import com.example.myapplication.adapter.lab6.Lab6ItemAdapter;
import com.example.myapplication.adapter.lab6.Lab6Listener;
import com.example.myapplication.controllers.lab1.Controller;
import com.example.myapplication.controllers.lab6.Lab6Controller;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Lab6Activity extends ComponentActivity {

    private Lab6LayoutBinding binding;

    private Lab6ItemAdapter adapter;

    private Lab6Controller mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = Lab6LayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo viewmodel
        mViewModel = new ViewModelProvider(this).get(Lab6Controller.class);
        mViewModel.init();

        //Đăng ký branch
        RetrofitClient.Companion.setBranch("1");

        mViewModel.registerDeviceApp(new RegisterContents(
                "test2@tmmn",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                "22184a05bdd68cb1",
                "123456",
                "1.5.7",
                "tuhDevice"));

        //Kiểm tra xem dữ liệu sau khi call api có hoạt động không
        observeDataToken();
        observeData();
        testObserve();

        btnConfirmHandle();
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
            CustomDialogJava6 dialog = new CustomDialogJava6(Lab6Activity.this, productList);
            dialog.show();
        });
    }

    private void setUpAdapter(List<Item6> itemList, List<String> productList,List<ProductType6> productTypeList) {
        adapter = new Lab6ItemAdapter(itemList, new Lab6Listener() {

            //Hàm này dùng để xử lý khi thêm sản phẩm
            @Override
            public void add() {

                List<String> addProductList=new ArrayList<>();
                for(String value:productList){
                    addProductList.add(value);
                }

                List<ProductType6> addProductTypeList=new ArrayList<>();
                for(ProductType6 productType6:productTypeList){
                    addProductTypeList.add(new ProductType6(productType6.getName(),new ArrayList<>(Arrays.asList(0, 0))));
                }

                adapter.add(new Item6(addProductList,addProductTypeList));
            }

            //Hàm này dùng để xử lý khi xóa sản phẩm
            @Override
            public void delete(Item6 item) {
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


    //Hàm này dùng để quan sát giá trị của token và userID có lấy được hay chưa
    private void observeDataToken() {
        mViewModel.combinedData.observe(this, data -> {
            if (mViewModel.dataToken.getValue() != null) {

                //Set token cho client
                RetrofitClient.Companion.setAuthToken(data.first);

                //Đây là chỗ xử lý bắt đầu call api lấy danh sách dữ liệu cần thiết
                /*--------------------------------------------------------------------------------*/
                //Call API lấy danh sách nhân viên
                SearchContents searchContents = new SearchContents(-1, 755);
                mViewModel.search(searchContents);

                //Call API lấy danh sách sản phẩm
                mViewModel.getGoods();
            }else{
                Log.e("DEBUG","Data Token is null");
            }
        });
    }

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
                List<Item6> itemList=new ArrayList<>();

                //Lấy danh sách sản phẩm
                List<Good> goodList=data.second;
                List<ProductType6> productTypeAdapterList=new ArrayList<>();
                for(Good good:goodList){
                    productTypeAdapterList.add(new ProductType6(good.getName(),new ArrayList<>(Arrays.asList(0, 0))));
                }

                //Gán dữ liệu vào danh sách sản phẩm
                List<String> productList;
                productList = data.first.stream()
                        .map(Staff::getName)
                        .collect(Collectors.toCollection(ArrayList::new));

                itemList.add(new Item6(productList,productTypeAdapterList));

                binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                setUpAdapter(itemList, productList,productTypeAdapterList);

            }catch (Exception er){
                Log.e("DEBUG","Error from observeData:"+er);
            }
        });
    }

    private void testObserve(){
        mViewModel.dataStaffList.observe(this,staffList -> {
            Log.i("DEBUG","StaffList:"+staffList.size());
        });

        mViewModel.dataGoodList.observe(this,goods -> {
            Log.i("DEBUG","Goods:"+goods.size());

        });

        mViewModel.dataToken.observe(this,token -> {
            Log.i("DEBUG","DataToken:"+token);

        });
    }

}
