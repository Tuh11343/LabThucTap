package com.example.myapplication.adapter.lab6;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.DialogProductTypeItemBinding;
import com.example.myapplication.model.lab6.ProductType6;

import java.util.List;

public class DialogProductTypeAdapter extends RecyclerView.Adapter<DialogProductTypeAdapter.ViewHolder> {

    private List<ProductType6> productTypeList;

    public DialogProductTypeAdapter(List<ProductType6> productTypeList) {
        this.productTypeList = productTypeList;
    }

    public List<ProductType6> getProductTypeList() {
        return productTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogProductTypeItemBinding binding = DialogProductTypeItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            ProductType6 productType = productTypeList.get(holder.getAbsoluteAdapterPosition());
            if (productType != null) {

                //Set index cho view
                holder.binding.index.setText(String.valueOf(holder.getAbsoluteAdapterPosition()));

                //Set product type name
                holder.binding.productTypeName.setText(productTypeList.get(holder.getAbsoluteAdapterPosition()).getName());

                //Set dữ liệu import và export
                holder.binding.importInput.setText(String.valueOf(productTypeList.get(holder.getAbsoluteAdapterPosition()).getInputList().get(0)));
                holder.binding.exportInput.setText(String.valueOf(productTypeList.get(holder.getAbsoluteAdapterPosition()).getInputList().get(1)));

            }
        } catch (Exception er) {
            Log.e("DEBUG", "Error from onBindViewHolder ProductTypeAdapter:" + er);
        }
    }

    @Override
    public int getItemCount() {
        return productTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        DialogProductTypeItemBinding binding;

        public ViewHolder(DialogProductTypeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
