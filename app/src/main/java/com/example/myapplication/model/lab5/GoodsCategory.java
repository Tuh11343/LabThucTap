package com.example.myapplication.model.lab5;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Entity(tableName = "GoodsCategory")
public class GoodsCategory {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    public GoodsCategory(int id, String name) {
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

    public static GoodsCategory getGoodsCategory(JsonObject jsonObject) {
        if (jsonObject.has("id") && jsonObject.has("name")) {
            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            return new GoodsCategory(id, name);
        }
        return null;
    }
}
