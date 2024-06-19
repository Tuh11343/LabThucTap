package com.example.myapplication.adapter.lab6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemView6Binding;
import com.example.myapplication.model.lab6.Item6;
import com.example.myapplication.model.lab6.Product6;
import com.example.myapplication.model.lab6.ProductType6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lab6ItemAdapter extends RecyclerView.Adapter<Lab6ItemAdapter.ViewHolder> {

    private List<Item6> itemList;
    private List<String> productChooseList; //Biến này dùng để lưu giá trị chọn product
    private Lab6Listener mListener;

    public Lab6ItemAdapter(List<Item6> itemAdapterList, Lab6Listener listener) {
        this.itemList = itemAdapterList;
        this.productChooseList = new ArrayList<>(Collections.nCopies(itemAdapterList.size(), ""));
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemView6Binding binding = ItemView6Binding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item6 item = itemList.get(holder.getBindingAdapterPosition());

        if (item != null) {
            holder.binding.index.setText(String.valueOf(holder.getBindingAdapterPosition()));

            //Khởi tạo adapter cho spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    holder.itemView.getContext(),
                    R.layout.spinner_text,
                    item.getProductList()
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.binding.spinnerProduct.setAdapter(adapter);

            //Khởi tạo adapter cho Product Type List
            ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter(item.getProductTypeList());
            holder.binding.productTypeList.setAdapter(productTypeAdapter);

            //Gán giá trị cho product spinner
            if (!productChooseList.get(holder.getBindingAdapterPosition()).isEmpty()) {
                holder.binding.spinnerProduct.setSelection(item.getProductList().indexOf(productChooseList.get(holder.getBindingAdapterPosition())));
            } else {
                //Khởi tạo nếu chưa có giá trị
                String initialValue = (String) holder.binding.spinnerProduct.getSelectedItem();
                productChooseList.set(holder.getBindingAdapterPosition(), initialValue);
            }

            //Xử lý khi spinner product thay đổi
            holder.binding.spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(pos);
                    productChooseList.set(holder.getBindingAdapterPosition(), selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            holder.binding.addMore.setOnClickListener(v -> mListener.add());

            holder.binding.delete.setOnClickListener(v -> mListener.delete(item));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //Hàm này dùng để lấy danh sách sản phẩm
    public List<Product6> getValues() {
        List<Product6> resultList = new ArrayList<>();

        for (int index = 0; index < itemList.size(); index++) {
            List<ProductType6> productType = new ArrayList<>();

            //Khởi tạo danh sách loại sản phẩm
            for (int productTypeIndex = 0; productTypeIndex < itemList.get(index).getProductTypeList().size(); productTypeIndex++) {
                productType.add(new ProductType6(itemList.get(index).getProductTypeList().get(productTypeIndex).getName(), itemList.get(index).getProductTypeList().get(productTypeIndex).getInputList()));
            }

            resultList.add(new Product6(productChooseList.get(index), productType));
        }

        return resultList;
    }

    public void add(Item6 item) {
        itemList.add(item);
        productChooseList.add(item.getProductList().get(0));
        notifyItemInserted(itemList.size() - 1);
    }

    public void delete(Item6 item) {
        int position = itemList.indexOf(item);
        if (position != -1) {
            productChooseList.remove(position);
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemView6Binding binding;

        public ViewHolder(ItemView6Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
