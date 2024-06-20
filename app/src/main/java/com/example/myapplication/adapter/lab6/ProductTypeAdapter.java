package com.example.myapplication.adapter.lab6;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ProductTypeItemViewBinding;
import com.example.myapplication.model.lab6.ProductType6;

import java.util.List;

public class ProductTypeAdapter extends RecyclerView.Adapter<ProductTypeAdapter.ViewHolder> {

    private List<ProductType6> productTypeList;

    public ProductTypeAdapter(List<ProductType6> productTypeList) {
        this.productTypeList = productTypeList;
    }

    public List<ProductType6> getProductTypeList() {
        return productTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ProductTypeItemViewBinding binding = ProductTypeItemViewBinding.inflate(inflater, parent, false);
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
                holder.binding.productTypeName.setText(productTypeList.get(holder.getBindingAdapterPosition()).getName());

                //Chỗ này bắt đầu xử lý call back cho input text
                /*--------------------------------------------------------------------------------*/
                //Loại bỏ call back của edit text tránh trường hợp lỗi phát sinh
                holder.binding.importInput.removeTextChangedListener((TextWatcher) holder.binding.importInput.getTag());
                holder.binding.exportInput.removeTextChangedListener((TextWatcher) holder.binding.exportInput.getTag());

                //Gán giá trị cho import và export
                holder.binding.importInput.setText(String.valueOf(productType.getInputList().get(0)));
                holder.binding.exportInput.setText(String.valueOf(productType.getInputList().get(1)));

                //Thêm callback cho input text
                TextWatcher importTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String textStr = s.toString();
                        long amount = textStr.isEmpty() ? 0 : Long.parseLong(textStr);
                        productType.getInputList().set(0,amount);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                holder.binding.importInput.addTextChangedListener(importTextWatcher);
                holder.binding.importInput.setTag(importTextWatcher);

                TextWatcher exportTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String textStr = s.toString();
                        long amount = textStr.isEmpty() ? 0 : Long.parseLong(textStr);
                        productType.getInputList().set(1,amount);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                holder.binding.exportInput.addTextChangedListener(exportTextWatcher);
                holder.binding.exportInput.setTag(exportTextWatcher);

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
        ProductTypeItemViewBinding binding;

        public ViewHolder(ProductTypeItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
