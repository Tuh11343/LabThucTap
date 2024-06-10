package com.example.myapplication.model.lab2

import com.example.myapplication.model.lab2.ProductType


class Item(var productList:MutableList<String>, var productTypeList:MutableList<ProductType>) {

    constructor():this(mutableListOf(),mutableListOf())

}