package com.example.myapplication.utils.lab6;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

public class LoadingDialog {

    private AlertDialog dialog;

    public LoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();
    }

    public void setDialog(boolean show) {
        if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}