package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.databinding.CustomSpinnerBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lab7Activity extends AppCompatActivity {

    private CustomSpinnerBinding binding;
    private List<String> data = new ArrayList<>(Arrays.asList("Test 1", "Test 2", "Test 3"));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CustomSpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.autoCompleteTextView.setAdapter(new ArrayAdapter<>(this, R.layout.custom_dropdown_item, data));
        binding.autoCompleteTextView.setOnItemClickListener((adapterView, view, position, l) -> {
            Toast.makeText(Lab7Activity.this, "Du lieu:" + data.get(position), Toast.LENGTH_SHORT).show();
        });

        binding.autoCompleteTextView.setText(binding.autoCompleteTextView.getAdapter().getItem(0).toString(),false);
        Log.i("DEBUG","Data:"+binding.autoCompleteTextView.getAdapter().getItem(0).toString());
    }

    //Hàm này dùng để clear focus tất cả edit text khi nhấn ra ngoài màn hình
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
