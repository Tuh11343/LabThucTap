package com.example.myapplication.adapter.lab6;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.DialogItem6Binding;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab6.Product6;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogAdapter6Java extends RecyclerView.Adapter<CustomDialogAdapter6Java.ViewHolder> {

    private List<Product6> productList;

    public CustomDialogAdapter6Java(List<Product6> productList) {
        this.productList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public CustomDialogAdapter6Java.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogItem6Binding binding = DialogItem6Binding.inflate(inflater, parent, false);
        return new CustomDialogAdapter6Java.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomDialogAdapter6Java.ViewHolder holder, int position) {
        Product6 product = productList.get(holder.getAbsoluteAdapterPosition());
        if (product != null) {

            holder.binding.index.setText(String.valueOf(holder.getAbsoluteAdapterPosition()));

            //Gán tên sản phẩm
            holder.binding.productName.setText(product.getProductName());

            //Tiến hành tạo adapter cho product type
            DialogProductTypeAdapter adapter=new DialogProductTypeAdapter(product.getProductTypeList());
            holder.binding.productTypeList.setAdapter(adapter);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DialogItem6Binding binding;

        public ViewHolder(DialogItem6Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
