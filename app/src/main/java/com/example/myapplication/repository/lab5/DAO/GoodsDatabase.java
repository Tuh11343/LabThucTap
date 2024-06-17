package com.example.myapplication.repository.lab5.DAO;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.model.lab5.Good;
import com.example.myapplication.model.lab5.GoodsCategory;

@Database(entities = {GoodsCategory.class, Good.class},version = 2)
public abstract class GoodsDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "goods-db";
    private static GoodsDatabase sInstance;

    public static synchronized GoodsDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            GoodsDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sInstance;
    }


    public abstract GoodsCategoryDAO getGoodsCategoryDAO();
    public abstract GoodsDAO getGoodsDAO();

}
