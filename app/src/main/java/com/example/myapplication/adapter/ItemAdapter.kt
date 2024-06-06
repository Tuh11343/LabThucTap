package com.example.myapplication.adapter

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Item
import com.example.myapplication.databinding.ItemViewBinding


class ItemAdapter(var itemList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var productChooseList= MutableList(itemList.size) { "" }

    inner class ViewHolder(val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[holder.absoluteAdapterPosition]
        if (item != null) {
            holder.binding.index.text=holder.absoluteAdapterPosition.toString()
            val adapter = ArrayAdapter(
                holder.itemView.context,
                R.layout.simple_spinner_item,
                item.productList
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            holder.binding.spinnerProduct.adapter = adapter

            holder.binding.productType1.text=item.productTypeList[0].name
            holder.binding.productType2.text=item.productTypeList[1].name
            holder.binding.productType3.text=item.productTypeList[2].name

            holder.binding.inputNumber1.doOnTextChanged { text, start, before, count ->
                if(text.toString().isNotEmpty()){
                    item.productTypeList[0].amount=Integer.parseInt(text.toString())
                }else{
                    item.productTypeList[0].amount=0
                }
            }

            holder.binding.inputNumber2.doOnTextChanged { text, start, before, count ->
                if(text.toString().isNotEmpty()){
                    item.productTypeList[1].amount=Integer.parseInt(text.toString())
                }else{
                    item.productTypeList[0].amount=0
                }
            }

            holder.binding.inputNumber3.doOnTextChanged { text, start, before, count ->
                if(text.toString().isNotEmpty()){
                    item.productTypeList[2].amount=Integer.parseInt(text.toString())
                }else{
                    item.productTypeList[0].amount=0
                }
            }

            val initialValue = holder.binding.spinnerProduct.selectedItem as String
            productChooseList[holder.absoluteAdapterPosition]=initialValue
            holder.binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position) as String
                    productChooseList[holder.absoluteAdapterPosition]=selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        }
    }

    fun getValues():MutableList<Product> {
        var resultList=mutableListOf<Product>()

        for((index, item) in itemList.withIndex()){
            var productType= mutableListOf<ProductType>()
            productType.add(ProductType(item.productTypeList[0].name,item.productTypeList[0].amount))
            productType.add(ProductType(item.productTypeList[1].name,item.productTypeList[1].amount))
            productType.add(ProductType(item.productTypeList[2].name,item.productTypeList[2].amount))

            resultList.add(Product(productChooseList[index],productType))
        }

        return resultList
    }


}

class ProductType(var name:String,var amount:Int){

}

class Product(var productName:String,var listProductType:MutableList<ProductType>)