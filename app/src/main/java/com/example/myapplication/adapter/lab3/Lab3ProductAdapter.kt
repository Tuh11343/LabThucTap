package com.example.myapplication.adapter.lab3

import android.R
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemView3Binding
import com.example.myapplication.model.lab2.Item
import com.example.myapplication.model.lab2.Product
import com.example.myapplication.model.lab2.ProductType


class Lab3ItemAdapter(var itemAdapterList: MutableList<Item>, var mListener: Lab3Listener) :
    RecyclerView.Adapter<Lab3ItemAdapter.ViewHolder>() {

    var productChooseList = MutableList(itemAdapterList.size) { "" }

    inner class ViewHolder(val binding: ItemView3Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemView3Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemAdapterList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itemAdapterList[holder.bindingAdapterPosition]
        if (item != null) {
            holder.binding.index.text = holder.bindingAdapterPosition.toString()
            val adapter = ArrayAdapter(
                holder.itemView.context, R.layout.simple_spinner_item, item.productList
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            holder.binding.spinnerProduct.adapter = adapter

            //Gán product type name
            holder.binding.productType1.text = item.productTypeList[0].name
            holder.binding.productType2.text = item.productTypeList[1].name
            holder.binding.productType3.text = item.productTypeList[2].name

            //Loại bỏ text watcher tránh trường hợp lỗi phát sinh
            holder.binding.inputNumber1.removeTextChangedListener(holder.binding.inputNumber1.tag as? TextWatcher)
            holder.binding.inputNumber2.removeTextChangedListener(holder.binding.inputNumber2.tag as? TextWatcher)
            holder.binding.inputNumber3.removeTextChangedListener(holder.binding.inputNumber3.tag as? TextWatcher)

            //Gán giá trị cho productTypeList
            holder.binding.inputNumber1.setText(item.productTypeList[0].amount.toString())
            holder.binding.inputNumber2.setText(item.productTypeList[1].amount.toString())
            holder.binding.inputNumber3.setText(item.productTypeList[2].amount.toString())

            //Thêm text watcher
            val textWatcher1 = holder.binding.inputNumber1.doOnTextChanged { text, _, _, _ ->
                item.productTypeList[0].amount = text?.toString()?.toIntOrNull() ?: 0
            }
            holder.binding.inputNumber1.tag = textWatcher1

            val textWatcher2 = holder.binding.inputNumber2.doOnTextChanged { text, _, _, _ ->
                item.productTypeList[1].amount = text?.toString()?.toIntOrNull() ?: 0
            }
            holder.binding.inputNumber2.tag = textWatcher2

            val textWatcher3 = holder.binding.inputNumber3.doOnTextChanged { text, _, _, _ ->
                item.productTypeList[2].amount = text?.toString()?.toIntOrNull() ?: 0
            }
            holder.binding.inputNumber3.tag = textWatcher3

            //Gán giá trị cho product spinner
            if (productChooseList[holder.bindingAdapterPosition] != null) {
                holder.binding.spinnerProduct.setSelection(
                    item.productList.indexOf(
                        productChooseList[holder.bindingAdapterPosition]
                    )
                )
            } else {
                //Khởi tạo dữ liệu nếu nó chưa có
                val initialValue = holder.binding.spinnerProduct.selectedItem as String
                productChooseList[holder.bindingAdapterPosition] = initialValue
            }

            //Xử lý sự kiện khi spinner product thay đổi
            holder.binding.spinnerProduct.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        val selectedItem = parent?.getItemAtPosition(position) as String
                        productChooseList[holder.bindingAdapterPosition] = selectedItem
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            holder.binding.addMore.setOnClickListener {
                mListener.add()
            }

            holder.binding.delete.setOnClickListener {
                mListener.delete(item)
            }

        }
    }

    fun getValues(): MutableList<Product> {
        var resultList = mutableListOf<Product>()

        for ((index, item) in itemAdapterList.withIndex()) {
            var productType = mutableListOf<ProductType>()
            productType.add(
                ProductType(
                    item.productTypeList[0].name, item.productTypeList[0].amount
                )
            )
            productType.add(
                ProductType(
                    item.productTypeList[1].name, item.productTypeList[1].amount
                )
            )
            productType.add(
                ProductType(
                    item.productTypeList[2].name, item.productTypeList[2].amount
                )
            )

            resultList.add(Product(productChooseList[index], productType))
        }

        return resultList
    }

    fun add(item: Item) {
        itemAdapterList.add(item)
        productChooseList.add(item.productList[0])
        notifyItemInserted(itemAdapterList.size - 1)
    }

    fun delete(item: Item) {
        var position = itemAdapterList.indexOf(item)
        if (position != -1) {
            productChooseList.removeAt(position)
            itemAdapterList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemAdapterList.size)
        }
    }


}
