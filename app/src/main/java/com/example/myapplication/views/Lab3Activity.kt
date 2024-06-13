package com.example.myapplication.views

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.utils.lab2.CustomDialog
import com.example.myapplication.adapter.ItemAdapter
import com.example.myapplication.adapter.lab3.Lab3ItemAdapter
import com.example.myapplication.adapter.lab3.Lab3Listener
import com.example.myapplication.databinding.Lab2LayoutBinding
import com.example.myapplication.model.lab2.Item
import com.example.myapplication.model.lab2.Product
import com.example.myapplication.model.lab2.ProductType

class Lab3Activity : ComponentActivity() {

    private lateinit var binding: Lab2LayoutBinding
    private lateinit var adapter:Lab3ItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = Lab2LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        var itemAdapterList = mutableListOf<Item>()

        val productTypeList1 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        var productList1 = mutableListOf("Mango", "Strawberry", "Avocado")
        itemAdapterList.add(Item(productList1, productTypeList1))

        adapter = Lab3ItemAdapter(itemAdapterList,object:Lab3Listener{
            override fun add() {
                var productList = mutableListOf("Mango", "Strawberry", "Avocado")
                val productTypeList = mutableListOf(
                    ProductType("Type 1", 0),
                    ProductType("Type 2", 0),
                    ProductType("Type 3", 0)
                )
                adapter.add(Item(productList,productTypeList))
            }

            override fun delete(item:Item) {
                adapter.delete(item)
            }

        })
        binding.recyclerView.adapter = adapter

        binding.btnConfirm.setOnClickListener {
            var productList = adapter.getValues()
            Log.i("DEBUG", "Size:${productList.size}")
            if (hasDuplicateProductName(productList)) {
                Toast.makeText(this, "San pham bi trung vui long nhap lai", Toast.LENGTH_SHORT)
                    .show()
            } else if (productHasEmptyAmount(productList)) {
                Toast.makeText(this, "Khong duoc de trong gia tri", Toast.LENGTH_SHORT).show()
            } else {
                val customDialog = CustomDialog(this, productList)
                customDialog.show()
            }
        }

    }

    private fun hasDuplicateProductName(productList: List<Product>): Boolean {
        val seenNames = mutableSetOf<String>()
        for (product in productList) {
            if (!seenNames.add(product.productName)) {
                return true
            }
        }
        return false
    }

    private fun productHasEmptyAmount(productList: List<Product>): Boolean {
        for (product in productList) {
            for (productType in product.listProductType) {
                if (productType.amount == 0)
                    return true
            }
        }
        return false
    }


}
