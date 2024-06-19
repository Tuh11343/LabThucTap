package com.example.myapplication.controllers.lab6;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class Lab6ControllerFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application mApplication;

    public Lab6ControllerFactory(@NonNull Application application) {
        super(application);
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(Lab6Controller.class)) {
            return (T) new Lab6Controller(mApplication);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
