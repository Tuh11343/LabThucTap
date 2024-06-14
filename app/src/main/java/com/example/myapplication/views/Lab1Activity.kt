package com.example.myapplication.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.controllers.lab1.Controller
import com.example.myapplication.model.lab1.RegisterContents
import com.example.myapplication.network.lab1.RetrofitClient
import com.example.myapplication.adapter.StaffAdapter
import com.example.myapplication.databinding.Lab1LayoutBinding
import com.example.myapplication.model.lab1.SearchContents

class Lab1Activity : ComponentActivity() {

    private lateinit var binding: Lab1LayoutBinding
    private lateinit var controller: Controller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding=Lab1LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = ViewModelProvider(this)[Controller::class.java]
        RetrofitClient.setBranch("1")


        binding.btnSignIn.setOnClickListener {
            btnSignInClick()
        }

        binding.btnSearch.setOnClickListener {
            btnSearchClick()
        }

        observeRegister()
        observeSearch()

    }


    private fun btnSignInClick() {

        if (binding.deviceName.text!!.isNotBlank()) {
            var registerContents = RegisterContents(
                "nvtest@tmmn",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                "22184a05bdd68cb1",
                "123456",
                "1.5.7",
                binding.deviceName.text.toString()
            )

            controller.registerDeviceApp(registerContents)
        } else {
            Toast.makeText(this, "Khong duoc de trong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun btnSearchClick() {
        if (controller.dataToken.value == null || controller.dataUserID.value == null) {
            Toast.makeText(this, "Chưa có token", Toast.LENGTH_SHORT).show()
        } else {
            var searchContents = SearchContents(-1, controller.dataUserID.value!!)
            controller.search(searchContents)
        }
    }

    private fun observeRegister() {
        controller.dataUserID.observe(this) { userID ->
            if (userID != null) {
                Toast.makeText(this, "Lấy thành công userID:${userID}", Toast.LENGTH_SHORT).show()
            }
        }

        controller.dataToken.observe(this) { token ->
            if(token!=null){
                Toast.makeText(this, "Lấy thành công token:${token}", Toast.LENGTH_SHORT).show()
                RetrofitClient.setAuthToken(token!!)
            }
        }
    }

    private fun observeSearch() {
        controller.dataStaffList.observe(this) { staffList ->
            if(staffList.isNotEmpty()){
                binding.staffList.adapter = StaffAdapter(staffList)
                Toast.makeText(this,"Lấy thành công danh sách", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

