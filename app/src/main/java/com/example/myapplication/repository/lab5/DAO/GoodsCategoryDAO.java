package com.example.myapplication.repository.lab5.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.model.lab5.GoodsCategory;

import java.util.List;

@Dao
public interface GoodsCategoryDAO {

    @Insert
    public void add(GoodsCategory goodsCategory);

    @Query("select * from GoodsCategory")
    public List<GoodsCategory> findAll();

}
