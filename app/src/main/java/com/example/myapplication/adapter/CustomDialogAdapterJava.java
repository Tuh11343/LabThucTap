package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.DialogItemBinding;
import com.example.myapplication.model.lab2.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomDialogAdapterJava extends RecyclerView.Adapter<CustomDialogAdapterJava.ViewHolder> {


    private List<Product> productList;

    public CustomDialogAdapterJava(List<Product> productList) {
        this.productList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public CustomDialogAdapterJava.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogItemBinding binding = DialogItemBinding.inflate(inflater, parent, false);
        return new CustomDialogAdapterJava.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomDialogAdapterJava.ViewHolder holder, int position) {
        Product product = productList.get(holder.getAbsoluteAdapterPosition());
        if (product != null) {

            //Binding dữ liệu lên view

            holder.binding.productName.setText(product.getProductName());

            holder.binding.productType1.setText(product.getListProductType().get(0).getName());
            holder.binding.productType2.setText(product.getListProductType().get(1).getName());
            holder.binding.productType3.setText(product.getListProductType().get(2).getName());


            holder.binding.productAmount1.setText(String.valueOf(product.getListProductType().get(0).getAmount()));
            holder.binding.productAmount2.setText(String.valueOf(product.getListProductType().get(1).getAmount()));
            holder.binding.productAmount3.setText(String.valueOf(product.getListProductType().get(2).getAmount()));


        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DialogItemBinding binding;

        public ViewHolder(DialogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
