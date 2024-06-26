package com.example.myapplication.utils.lab6;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int marginBottom;

    public MarginItemDecoration(int marginBottom) {
        this.marginBottom = marginBottom;
    }
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Add bottom margin to each item
        outRect.bottom = marginBottom;
    }
}
