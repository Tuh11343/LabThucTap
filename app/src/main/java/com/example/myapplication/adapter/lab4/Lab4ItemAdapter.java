package com.example.myapplication.adapter.lab4;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemView3Binding;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;

import java.util.ArrayList;
import java.util.List;

public class Lab4ItemAdapter extends RecyclerView.Adapter<Lab4ItemAdapter.ViewHolder> {

    private List<Item> itemAdapterList;
    private Lab4Listener mListener;
    private List<String> productChooseList;

    public Lab4ItemAdapter(List<Item> itemAdapterList, Lab4Listener mListener) {
        this.itemAdapterList = itemAdapterList;
        this.mListener = mListener;
        this.productChooseList = new ArrayList<>(itemAdapterList.size());
        for (int i = 0; i < itemAdapterList.size(); i++) {
            this.productChooseList.add("");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemView3Binding binding;

        public ViewHolder(ItemView3Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemView3Binding binding = ItemView3Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemAdapterList.get(holder.getAbsoluteAdapterPosition());

        holder.binding.index.setText(String.valueOf(holder.getAbsoluteAdapterPosition()));

        Context context = holder.itemView.getContext();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                item.getProductList()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        holder.binding.spinnerProduct.setAdapter(adapter);

        holder.binding.productType1.setText(item.getProductTypeList().get(0).getName());
        holder.binding.productType2.setText(item.getProductTypeList().get(1).getName());
        holder.binding.productType3.setText(item.getProductTypeList().get(2).getName());


        holder.binding.inputNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int end, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    item.getProductTypeList().get(0).setAmount(Integer.parseInt(charSequence.toString()));
                } else {
                    item.getProductTypeList().get(0).setAmount(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.binding.inputNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    item.getProductTypeList().get(1).setAmount(Integer.parseInt(s.toString()));
                } else {
                    item.getProductTypeList().get(1).setAmount(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.binding.inputNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    item.getProductTypeList().get(2).setAmount(Integer.parseInt(s.toString()));
                } else {
                    item.getProductTypeList().get(2).setAmount(0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        String initialValue = (String) holder.binding.spinnerProduct.getSelectedItem();
        productChooseList.set(holder.getAbsoluteAdapterPosition(), initialValue);


        holder.binding.spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                productChooseList.set(holder.getAbsoluteAdapterPosition(), selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.binding.addMore.setOnClickListener(v -> mListener.add());

        holder.binding.delete.setOnClickListener(v -> mListener.delete(item));
    }

    @Override
    public int getItemCount() {
        return itemAdapterList.size();
    }

    public List<Product> getValues() {
        List<Product> resultList = new ArrayList<>();

        for (int index = 0; index < itemAdapterList.size(); index++) {
            Item item = itemAdapterList.get(index);
            List<ProductType> productType = new ArrayList<>();
            productType.add(new ProductType(item.getProductTypeList().get(0).getName(), item.getProductTypeList().get(0).getAmount()));
            productType.add(new ProductType(item.getProductTypeList().get(1).getName(), item.getProductTypeList().get(1).getAmount()));
            productType.add(new ProductType(item.getProductTypeList().get(2).getName(), item.getProductTypeList().get(2).getAmount()));

            resultList.add(new Product(productChooseList.get(index), productType));
        }

        return resultList;
    }

    public void add(Item item) {
        itemAdapterList.add(item);
        productChooseList.add(item.getProductList().get(0));
        notifyItemInserted(itemAdapterList.size());
    }

    public void delete(Item item) {
        int position = itemAdapterList.indexOf(item);
        productChooseList.remove(position);
        itemAdapterList.remove(position);
        notifyItemRemoved(position);
    }
}
