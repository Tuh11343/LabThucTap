package com.example.myapplication.utils.lab6;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.CustomDialogAdapterJava;
import com.example.myapplication.adapter.lab6.CustomDialogAdapter6Java;
import com.example.myapplication.databinding.CustomDialog6Binding;
import com.example.myapplication.databinding.CustomDialogBinding;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab6.Product6;

import java.util.List;


public class CustomDialogJava6 extends Dialog {

    private CustomDialog6Binding binding;
    private List<Product6> productList;
    private Context context;


    public CustomDialogJava6(@NonNull Context context, List<Product6> productList) {
        super(context);
        this.context=context;
        this.productList=productList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Khởi tạo layout cho view
        binding=CustomDialog6Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Điều chỉnh dài rộng cho dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        //Khởi tạo adapter cho dialog
        CustomDialogAdapter6Java adapter=new CustomDialogAdapter6Java(productList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));

        //Tắt dialog khi nhấn vào button
        binding.btnConfirm.setOnClickListener(view -> {
           dismiss();
        });
    }
}
