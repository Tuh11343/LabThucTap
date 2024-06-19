package com.example.myapplication

import android.app.Application
import com.example.myapplication.repository.lab5.DAO.GoodsDatabase

class AndroidApp : Application() {

    lateinit var mDatabase: GoodsDatabase

    override fun onCreate() {
        super.onCreate()
        mDatabase= GoodsDatabase.getInstance(this);
    }
}