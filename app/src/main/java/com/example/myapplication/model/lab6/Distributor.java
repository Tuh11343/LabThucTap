package com.example.myapplication.model.lab6;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Distributor {

    private int id;
    private String name;

    public Distributor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Distributor> getDistributorList(JsonArray jsonArray) {

        List<Distributor> distributorList = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            distributorList.add(new Distributor(id,name));
        }

        return distributorList;
    }
}
