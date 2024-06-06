package com.example.myapplication

import com.example.myapplication.adapter.ProductType

class Item(var productList:MutableList<String>,var productTypeList:MutableList<ProductType>) {

    constructor():this(mutableListOf(),mutableListOf())

}