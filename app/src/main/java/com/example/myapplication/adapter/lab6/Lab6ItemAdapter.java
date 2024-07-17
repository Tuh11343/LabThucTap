package com.example.myapplication.adapter.lab6;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
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

    private final List<Item6> itemList;
    private final List<String> productChooseList; //Biến này dùng để lưu giá trị chọn product từ product spinner
    private final Lab6Listener mListener;

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
        Item6 item = itemList.get(holder.getAbsoluteAdapterPosition());

        if (item != null) {
            holder.binding.index.setText(String.valueOf(holder.getAbsoluteAdapterPosition()+1));

            //Khởi tạo adapter cho spinner
            /*ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    holder.itemView.getContext(),
                    R.layout.spinner_text,
                    item.getProductList()
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.binding.spinnerProduct.setAdapter(adapter);*/
            holder.binding.autoCompleteTextView.setAdapter(new ArrayAdapter<>(holder.itemView.getContext(), R.layout.custom_dropdown_item, item.getProductList()));
            if (productChooseList.get(holder.getAbsoluteAdapterPosition()).isEmpty()) {//Nếu chưa có giá trị thì khởi tạo

                String value=holder.binding.autoCompleteTextView.getAdapter().getItem(0).toString();

                productChooseList.set(holder.getAbsoluteAdapterPosition(), value);
                holder.binding.autoCompleteTextView.setText(value,false);

            }else{//Nếu có giá trị thì set cho spinner
                holder.binding.autoCompleteTextView.setText(productChooseList.get(holder.getAbsoluteAdapterPosition()),false);
            }

            //Xử lý khi spinner thay đổi
            holder.binding.autoCompleteTextView.setOnItemClickListener((adapterView, view, spinnerPosition, l) -> {
                productChooseList.set(holder.getAbsoluteAdapterPosition(), item.getProductList().get(spinnerPosition));
            });

            //Khởi tạo adapter cho Product Type List
            ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter(item.getProductTypeList());
            holder.binding.productTypeList.setAdapter(productTypeAdapter);
            holder.binding.productTypeList.addItemDecoration(new DividerItemDecoration(holder.itemView.getContext(), DividerItemDecoration.VERTICAL));

            /*//Gán giá trị cho product spinner
            if (!productChooseList.get(holder.getAbsoluteAdapterPosition()).isEmpty()) {
                holder.binding.spinnerProduct.setSelection(item.getProductList().indexOf(productChooseList.get(holder.getAbsoluteAdapterPosition())));
            } else {
                //Khởi tạo nếu chưa có giá trị
                String initialValue = (String) holder.binding.spinnerProduct.getSelectedItem();
                productChooseList.set(holder.getAbsoluteAdapterPosition(), initialValue);
            }

            //Xử lý khi spinner product thay đổi
            holder.binding.spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(pos);
                    productChooseList.set(holder.getAbsoluteAdapterPosition(), selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });*/

            //Đăng ký call back cho nút thêm và xóa
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


    //Hàm này dùng để thêm item vào trong adapter
    public void add(Item6 item) {
        itemList.add(item);
        productChooseList.add(item.getProductList().get(0));
        notifyItemInserted(itemList.size() - 1);
    }

    //Hàm này dùng để xóa item khỏi adapter
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
