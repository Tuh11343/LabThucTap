package com.example.myapplication.model.lab6;

import java.util.List;

public class ProductType6 {

    String name;
    List<Integer> inputList;

    public ProductType6(String name, List<Integer> inputList) {
        this.name = name;
        this.inputList = inputList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getInputList() {
        return inputList;
    }

    public void setInputList(List<Integer> inputList) {
        this.inputList = inputList;
    }
}
