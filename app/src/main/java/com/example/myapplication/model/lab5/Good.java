package com.example.myapplication.model.lab5;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Good")
public class Good {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    public Good(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public static List<Good> getGoods(JsonArray jsonArray) {

        List<Good> goodList = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            double price = jsonObject.get("price").getAsDouble();
            goodList.add(new Good(id, name, price));
        }

        return goodList;
    }
}
