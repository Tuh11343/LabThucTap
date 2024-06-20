package com.example.myapplication.controllers.lab3

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class Lab3ControllerFactory(private val mApplication: Application) :
    ViewModelProvider.AndroidViewModelFactory(mApplication) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Lab3Controller::class.java)) {
            return Lab3Controller(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}