package com.example.myapplication.repository.lab5.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.lab5.Good;

import java.util.List;

@Dao
public interface GoodsDAO {

    @Insert
    public void add(Good good);

    @Update
    void update(Good good);

    @Query("SELECT COUNT(*) FROM Good")
    int getCount();

    @Query("Delete from Good")
    void deleteAll();

    @Query("select * from Good")
    public List<Good> findAll();

}
