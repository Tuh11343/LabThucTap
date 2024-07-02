package com.example.myapplication.utils.lab6;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.myapplication.adapter.lab6.FinalDialogAdapter;
import com.example.myapplication.databinding.FinalDialogBinding;
import com.example.myapplication.model.lab6.Product6;

import java.util.List;

public class FinalCustomDialog extends Dialog {

    private FinalDialogBinding binding;
    private List<Product6> productList;
    private Context context;


    public FinalCustomDialog(@NonNull Context context, List<Product6> productList) {
        super(context);
        this.productList=productList;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Khởi tạo layout cho view
        binding=FinalDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Điều chỉnh dài rộng cho dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        //Không cho người dùng bấm ra bên ngoài tắt
        setCancelable(false);

        //Khởi tạo adapter cho dialog
        FinalDialogAdapter adapter=new FinalDialogAdapter(productList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));

        //Call api để làm gì đó khi xác nhận
        binding.btnConfirm.setOnClickListener(view -> {
            dismiss();
        });

        binding.btnCancel.setOnClickListener(view->{
            dismiss();
        });


    }
}
