package com.example.myapplication.adapter.lab4;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemView3Binding;
import com.example.myapplication.model.lab2.Item;
import com.example.myapplication.model.lab2.Product;
import com.example.myapplication.model.lab2.ProductType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lab4ItemAdapter extends RecyclerView.Adapter<Lab4ItemAdapter.ViewHolder> {

    private List<Item> itemAdapterList;
    private List<String> productChooseList;
    private Lab4Listener mListener;

    public Lab4ItemAdapter(List<Item> itemAdapterList, Lab4Listener listener) {
        this.itemAdapterList = itemAdapterList;
        this.productChooseList = new ArrayList<>(Collections.nCopies(itemAdapterList.size(),""));
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemView3Binding binding;

        public ViewHolder(ItemView3Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemView3Binding binding = ItemView3Binding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemAdapterList.get(holder.getBindingAdapterPosition());

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

            //Gán product type name
            holder.binding.productType1.setText(item.getProductTypeList().get(0).getName());
            holder.binding.productType2.setText(item.getProductTypeList().get(1).getName());
            holder.binding.productType3.setText(item.getProductTypeList().get(2).getName());

            //Loại bỏ text watcher tránh trường hợp lỗi phát sinh
            holder.binding.inputNumber1.removeTextChangedListener((TextWatcher) holder.binding.inputNumber1.getTag());
            holder.binding.inputNumber2.removeTextChangedListener((TextWatcher) holder.binding.inputNumber2.getTag());
            holder.binding.inputNumber3.removeTextChangedListener((TextWatcher) holder.binding.inputNumber3.getTag());

            //Gán giá trị cho productTypeList
            holder.binding.inputNumber1.setText(String.valueOf(item.getProductTypeList().get(0).getAmount()));
            holder.binding.inputNumber2.setText(String.valueOf(item.getProductTypeList().get(1).getAmount()));
            holder.binding.inputNumber3.setText(String.valueOf(item.getProductTypeList().get(2).getAmount()));

            //Thêm text watcher
            TextWatcher textWatcher1 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String textStr = s.toString();
                    int amount = textStr.isEmpty() ? 0 : Integer.parseInt(textStr);
                    item.getProductTypeList().get(0).setAmount(amount);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.binding.inputNumber1.addTextChangedListener(textWatcher1);
            holder.binding.inputNumber1.setTag(textWatcher1);

            TextWatcher textWatcher2 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String textStr = s.toString();
                    int amount = textStr.isEmpty() ? 0 : Integer.parseInt(textStr);
                    item.getProductTypeList().get(1).setAmount(amount);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.binding.inputNumber2.addTextChangedListener(textWatcher2);
            holder.binding.inputNumber2.setTag(textWatcher2);

            TextWatcher textWatcher3 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String textStr = s.toString();
                    int amount = textStr.isEmpty() ? 0 : Integer.parseInt(textStr);
                    item.getProductTypeList().get(2).setAmount(amount);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.binding.inputNumber3.addTextChangedListener(textWatcher3);
            holder.binding.inputNumber3.setTag(textWatcher3);

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
        return itemAdapterList.size();
    }

    public List<Product> getValues() {
        List<Product> resultList = new ArrayList<>();

        for (int index = 0; index < itemAdapterList.size(); index++) {
            List<ProductType> productType = new ArrayList<>();
            productType.add(new ProductType(itemAdapterList.get(index).getProductTypeList().get(0).getName(),
                    itemAdapterList.get(index).getProductTypeList().get(0).getAmount()));
            productType.add(new ProductType(itemAdapterList.get(index).getProductTypeList().get(1).getName(),
                    itemAdapterList.get(index).getProductTypeList().get(1).getAmount()));
            productType.add(new ProductType(itemAdapterList.get(index).getProductTypeList().get(2).getName(),
                    itemAdapterList.get(index).getProductTypeList().get(2).getAmount()));

            resultList.add(new Product(productChooseList.get(index), productType));
        }

        return resultList;
    }

    public void add(Item item) {
        itemAdapterList.add(item);
        productChooseList.add(item.getProductList().get(0));
        notifyItemInserted(itemAdapterList.size() - 1);
    }

    public void delete(Item item) {
        int position = itemAdapterList.indexOf(item);
        if (position != -1) {
            productChooseList.remove(position);
            itemAdapterList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemAdapterList.size());
        }
    }
}
