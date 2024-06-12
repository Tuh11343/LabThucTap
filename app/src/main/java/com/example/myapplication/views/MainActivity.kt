package com.example.myapplication.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.databinding.MenuLayoutBinding

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

        binding.btnLab3.setOnClickListener{
            startActivity(Intent(this,Lab3Activity::class.java))
        }

        binding.btnLab4.setOnClickListener{
            startActivity(Intent(this,Lab4Activity::class.java))
        }
    }



}



