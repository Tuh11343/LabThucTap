package com.example.myapplication.views

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.utils.lab2.CustomDialog
import com.example.myapplication.adapter.ItemAdapter
import com.example.myapplication.databinding.Lab2LayoutBinding
import com.example.myapplication.model.lab2.Item
import com.example.myapplication.model.lab2.Product
import com.example.myapplication.model.lab2.ProductType

class Lab2Activity : ComponentActivity() {

    private lateinit var binding: Lab2LayoutBinding
    private var productList = mutableListOf("Mango", "Strawberry", "Avocado")


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

        val productTypeList2 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        val productTypeList3 = mutableListOf(
            ProductType("Type 1", 0),
            ProductType("Type 2", 0),
            ProductType("Type 3", 0)
        )

        itemAdapterList.add(Item(productList, productTypeList1))
        itemAdapterList.add(Item(productList, productTypeList2))
        itemAdapterList.add(Item(productList, productTypeList3))

        var adapter = ItemAdapter(itemAdapterList)
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
                Toast.makeText(this, "Thanh cong", Toast.LENGTH_SHORT).show()
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
