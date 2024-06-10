package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.DialogItemBinding
import com.example.myapplication.model.lab2.Product


class CustomDialogAdapter(var productList: MutableList<Product>) :
    RecyclerView.Adapter<CustomDialogAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: DialogItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DialogItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[holder.absoluteAdapterPosition]
        if (product != null) {

            holder.binding.productName.text=product.productName

            holder.binding.productType1.text=product.listProductType[0].name
            holder.binding.productType2.text=product.listProductType[1].name
            holder.binding.productType3.text=product.listProductType[2].name

            holder.binding.productAmount1.text=product.listProductType[0].amount.toString()
            holder.binding.productAmount2.text=product.listProductType[1].amount.toString()
            holder.binding.productAmount3.text=product.listProductType[2].amount.toString()

        }
    }




}
