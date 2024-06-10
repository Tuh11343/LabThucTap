package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.databinding.MenuLayoutBinding
import com.example.myapplication.views.Lab1Activity
import com.example.myapplication.views.Lab2Activity

class MainActivity : ComponentActivity() {

    private lateinit var binding: MenuLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=MenuLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLab1.setOnClickListener{
            startActivity(Intent(this,Lab1Activity::class.java))
        }

        binding.btnLab2.setOnClickListener{
            startActivity(Intent(this,Lab2Activity::class.java))
        }


    }



}



