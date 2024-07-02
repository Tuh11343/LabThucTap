package com.example.myapplication.adapter.lab3

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.lab6.ProductTypeAdapter
import com.example.myapplication.databinding.ItemView6Binding
import com.example.myapplication.model.lab6.Item6
import com.example.myapplication.model.lab6.Product6
import com.example.myapplication.model.lab6.ProductType6
import java.util.Collections

class Lab3ItemAdapter(
    private val itemList: MutableList<Item6>,
    private val mListener: Lab3Listener
) :
    RecyclerView.Adapter<Lab3ItemAdapter.ViewHolder>() {
    private val productChooseList //Biến này dùng để lưu giá trị chọn product từ product spinner
            : MutableList<String>

    init {
        productChooseList = ArrayList(
            Collections.nCopies(
                itemList.size, ""
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemView6Binding = ItemView6Binding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try{
            val item = itemList[holder.bindingAdapterPosition]
            holder.binding.index.text = holder.bindingAdapterPosition.toString()

            //Khởi tạo adapter cho spinner
            val adapter = ArrayAdapter(
                holder.itemView.context,
                R.layout.custom_dropdown_item,
                item.productList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            /*holder.binding.spinnerProduct.adapter = adapter*/

            //Khởi tạo adapter cho Product Type List
            val productTypeAdapter = ProductTypeAdapter(item.productTypeList)
            holder.binding.productTypeList.adapter = productTypeAdapter

            /*//Gán giá trị cho product spinner
            if (productChooseList[holder.bindingAdapterPosition].isNotEmpty()) {
                holder.binding.spinnerProduct.setSelection(
                    item.productList.indexOf(
                        productChooseList[holder.bindingAdapterPosition]
                    )
                )
            } else {
                //Khởi tạo nếu chưa có giá trị
                val initialValue = holder.binding.spinnerProduct.selectedItem as String
                productChooseList[holder.bindingAdapterPosition] = initialValue
            }

            //Xử lý khi spinner product thay đổi
            holder.binding.spinnerProduct.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(pos) as String
                    productChooseList[holder.bindingAdapterPosition] = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }*/

            //Đăng ký call back cho nút thêm và xóa
            holder.binding.addMore.setOnClickListener { mListener.add() }
            holder.binding.delete.setOnClickListener { mListener.delete(item) }
        }catch (e:Exception){
            Log.e("DEBUG","Error from bind viewholder:${e}")
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun getValues(): List<Product6> {
        val resultList: MutableList<Product6> = ArrayList()
        for (index in itemList.indices) {
            val productType: MutableList<ProductType6> = ArrayList()

            //Khởi tạo danh sách loại sản phẩm
            for (productTypeIndex in itemList[index].productTypeList.indices) {
                productType.add(
                    ProductType6(
                        itemList[index].productTypeList[productTypeIndex].name,
                        itemList[index].productTypeList[productTypeIndex].inputList
                    )
                )
            }
            resultList.add(Product6(productChooseList[index], productType))
        }
        return resultList
    }

    //Hàm này dùng để thêm item vào trong adapter
    fun add(item: Item6) {
        itemList.add(item)
        productChooseList.add(item.productList[0])
        notifyItemInserted(itemList.size - 1)
    }

    //Hàm này dùng để xóa item khỏi adapter
    fun delete(item: Item6) {
        val position = itemList.indexOf(item)
        if (position != -1) {
            productChooseList.removeAt(position)
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size)
        }
    }

    class ViewHolder(binding: ItemView6Binding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ItemView6Binding

        init {
            this.binding = binding
        }
    }
}