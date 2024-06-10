package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.adapter.CustomDialogAdapter
import com.example.myapplication.databinding.CustomDialogBinding
import com.example.myapplication.model.lab2.Product

class CustomDialog(context: Context, var productList:MutableList<Product>) : Dialog(context) {

    private lateinit var binding:CustomDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding=CustomDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        var adapter=CustomDialogAdapter(productList)
        binding.recyclerView.adapter=adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.btnConfirm.setOnClickListener{
            dismiss()
        }
    }

    // Các phương thức khác bạn có thể thêm vào đây nếu cần
}