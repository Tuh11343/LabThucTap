package com.example.myapplication.model.lab6;

import java.util.List;

public class ProductType6 {

    String name;
    List<Long> inputList;

    public ProductType6(String name, List<Long> inputList) {
        this.name = name;
        this.inputList = inputList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getInputList() {
        return inputList;
    }

    public void setInputList(List<Long> inputList) {
        this.inputList = inputList;
    }
}
