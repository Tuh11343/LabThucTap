package com.example.myapplication.adapter.lab6;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.FinalDialogBinding;
import com.example.myapplication.databinding.FinalDialogItemBinding;
import com.example.myapplication.model.lab6.Product6;
import com.example.myapplication.model.lab6.ProductType6;

import java.util.List;

public class FinalDialogAdapter extends RecyclerView.Adapter<FinalDialogAdapter.ViewHolder> {

    private List<Product6> productList;

    public FinalDialogAdapter(List<Product6> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FinalDialogItemBinding binding = FinalDialogItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Product6 product = productList.get(holder.getAbsoluteAdapterPosition());

            int index=holder.getAbsoluteAdapterPosition()+1;
            StringBuilder result = new StringBuilder(index + ". " + product.getProductName() + "\n");
            for (int i = 0; i < product.getProductTypeList().size(); i++) {
                ProductType6 productType6 = product.getProductTypeList().get(i);
                String productTypeName = productType6.getName();
                String input = productType6.getInputList().get(0).toString();
                String output = productType6.getInputList().get(1).toString();

                result.append("-").append(productTypeName).append(": Nhập ").append(input).append(", Xuất ").append(output);

                if (i < product.getProductTypeList().size() - 1) {
                    result.append("\n");
                }
            }

            holder.binding.content.setText(result);

        } catch (Exception er) {
            Log.e("DEBUG", "Error from FinalDialog Adapter onBindViewHolder:" + er);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FinalDialogItemBinding binding;

        public ViewHolder(FinalDialogItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
