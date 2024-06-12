package com.example.myapplication.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.lab4.Lab4ItemAdapter;
import com.example.myapplication.adapter.lab4.Lab4Listener;
import com.example.myapplication.databinding.Lab2LayoutBinding;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;
import com.example.myapplication.utils.lab2.CustomDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lab4Activity extends ComponentActivity {

    private Lab2LayoutBinding binding;
    private Lab4ItemAdapter adapter;

    private List<String> productList = Arrays.asList("Mango", "Strawberry", "Avocado");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = Lab2LayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        List<Item> itemAdapterList = new ArrayList<>();
        List<ProductType> productTypeList = new ArrayList<>();

        Collections.addAll(productTypeList,
                new ProductType("Type 1", 0),
                new ProductType("Type 2", 0),
                new ProductType("Type 3", 0)
        );

        itemAdapterList.add(new Item(productList, productTypeList));

        adapter = new Lab4ItemAdapter(itemAdapterList, new Lab4Listener() {
            @Override
            public void add() {
                List<ProductType> productTypeList = new ArrayList<>();
                Collections.addAll(productTypeList,
                        new ProductType("Type 1", 0),
                        new ProductType("Type 2", 0),
                        new ProductType("Type 3", 0)
                );
                adapter.add(new Item(productList, productTypeList));
            }

            @Override
            public void delete(Item item) {
                adapter.delete(item);
            }
        });
        binding.recyclerView.setAdapter(adapter);

        binding.btnConfirm.setOnClickListener(view -> {
            List<Product> productList = adapter.getValues();
            Log.i("DEBUG", "Size:" + productList.size());
            if (hasDuplicateProductName(productList)){
                Toast.makeText(Lab4Activity.this, "San pham bi trung vui long nhap lai", Toast.LENGTH_SHORT).show();
            }else if(productHasEmptyAmount(productList)){
            Toast.makeText(Lab4Activity.this, "Khong duoc de trong gia tri", Toast.LENGTH_SHORT).show();
            }else{
                CustomDialog dialog= new CustomDialog(Lab4Activity.this,productList);
                dialog.show();
            }

        });

    }

    private boolean hasDuplicateProductName(List<Product> productList) {
        List<String> seenNames = new ArrayList<>();
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

}
