package com.example.myapplication.model.lab6;

import java.util.List;

public class Product6 {

    String productName;
    List<ProductType6> productTypeList;

    public Product6(String productName, List<ProductType6> productTypeList) {
        this.productName = productName;
        this.productTypeList = productTypeList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<ProductType6> getProductTypeList() {
        return productTypeList;
    }

    public void setProductTypeList(List<ProductType6> productTypeList) {
        this.productTypeList = productTypeList;
    }
}
