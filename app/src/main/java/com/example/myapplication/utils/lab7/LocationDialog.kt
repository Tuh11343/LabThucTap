package com.example.myapplication.utils.lab7

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.example.myapplication.databinding.Lab7DialogBinding
import com.example.myapplication.model.lab7.GeoLocation

class LocationDialog(context: Context,var geoLocation: GeoLocation): Dialog(context) {

    private lateinit var binding:Lab7DialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Khởi tạo view
        binding= Lab7DialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Điều chỉnh dài rộng cho dialog
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        //Không cho người dùng bấm ra bên ngoài tắt
        setCancelable(false)

        binding.address.text=geoLocation.address
        binding.latLNG.text="${geoLocation.latitude}, ${geoLocation.longitude}"

        //Call api để làm gì đó khi xác nhận
        binding.btnConfirm.setOnClickListener { view -> dismiss() }
    }
}