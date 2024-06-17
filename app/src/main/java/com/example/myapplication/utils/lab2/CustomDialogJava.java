package com.example.myapplication.utils.lab2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.CustomDialogAdapterJava;
import com.example.myapplication.databinding.CustomDialogBinding;
import com.example.myapplication.model.lab2.Product;

import java.util.List;


public class CustomDialogJava extends Dialog {

    private CustomDialogBinding binding;
    private List<Product> productList;
    private Context context;


    public CustomDialogJava(@NonNull Context context,List<Product> productList) {
        super(context);
        this.context=context;
        this.productList=productList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Khởi tạo layout cho view
        binding=CustomDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Điều chỉnh dài rộng cho dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        //Khởi tạo adapter cho dialog
        CustomDialogAdapterJava adapter=new CustomDialogAdapterJava(productList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));

        //Tắt dialog khi nhấn vào button
        binding.btnConfirm.setOnClickListener(view -> {
           dismiss();
        });
    }
}
