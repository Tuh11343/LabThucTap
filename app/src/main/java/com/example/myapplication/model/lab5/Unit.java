package com.example.myapplication.model.lab5;


import android.util.Log;

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

@Entity(tableName = "Unit",foreignKeys = @ForeignKey(
        entity = Good.class,
        parentColumns = "id",
        childColumns = "good_id",
        onDelete = ForeignKey.CASCADE,onUpdate = ForeignKey.CASCADE
),
        indices = @Index("good_id"))
public class Unit {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "good_id")
    private int good_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "quantity")
    private int quantity;

    public Unit(int id, int good_id, String name, double price, int quantity) {
        this.id = id;
        this.good_id = good_id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getGood_id() {
        return good_id;
    }

    public void setGood_id(int good_id) {
        this.good_id = good_id;
    }

    public static Unit getUnit(int good_id,JsonObject jsonObject) {
        if (jsonObject.has("id") && jsonObject.has("name") && jsonObject.has("quantity") && jsonObject.has("price")) {
            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            int quantity = jsonObject.get("quantity").getAsInt();
            double price = jsonObject.get("price").getAsDouble();
            return new Unit(id,good_id, name, price, quantity);
        }
        return null;
    }

    public static List<Unit> getUnits(int good_id,JsonArray jsonArray) {

        List<Unit> unitList = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            double price = jsonObject.get("price").getAsDouble();
            int quantity=jsonObject.get("quantity").getAsInt();
            unitList.add(new Unit(id,good_id,name,price,quantity));
        }

        return unitList;
    }
}
