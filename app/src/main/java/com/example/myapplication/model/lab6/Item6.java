package com.example.myapplication.model.lab6;

import java.util.List;

public class Item6 {

    private List<String> productList;
    private List<ProductType6> productTypeList;

    public Item6(List<String> productList, List<ProductType6> productTypeList) {
        this.productList = productList;
        this.productTypeList = productTypeList;
    }

    public List<String> getProductList() {
        return productList;
    }

    public void setProductList(List<String> productList) {
        this.productList = productList;
    }

    public List<ProductType6> getProductTypeList() {
        return productTypeList;
    }

    public void setProductTypeList(List<ProductType6> productTypeList) {
        this.productTypeList = productTypeList;
    }
}
