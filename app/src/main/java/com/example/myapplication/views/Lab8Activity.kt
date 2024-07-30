package com.example.myapplication.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.Lab8LayoutBinding
import com.example.myapplication.services.SocketManager
import com.example.myapplication.services.SocketService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class Lab8Activity : AppCompatActivity() {

    private lateinit var binding: Lab8LayoutBinding

    //List các quyền
    companion object {
        private val PERMISSION = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        )
    }

    //Hàm này dùng để yêu cầu người dùng cấp quyền
    private fun checkPermission(): Boolean {
        return PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                // Kiểm tra xem người dùng đã cấp quyền hay chưa
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

                    //Tiến hành xử lý quá trình lấy token
                    getDeviceToken()
                } else {

                    Toast.makeText(
                        this,
                        "Không thể khởi chạy thông báo nếu chưa cấp quyền",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

            else -> {
                // Xử lý các requestCode khác nếu có
            }
        }
    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i("DEBUG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            //Lấy token
            val token = task.result

            Toast.makeText(this, "Token:$token", Toast.LENGTH_SHORT).show()

            /*//Gửi token tới server side
            binding.token.setText(token.toString())*/

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = Lab8LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*if(checkPermission()){
            getDeviceToken()
        }*/

        SocketManager.connect()

        // Khởi động dịch vụ SocketService
        val serviceIntent = Intent(this, SocketService::class.java)
        startService(serviceIntent)

        binding.btnSend.setOnClickListener {
            Log.i("DEBUG", "Click")
            val message = "Gửi từ client"
            if (message.isNotEmpty()) {
                SocketManager.sendMessage(message)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.disconnect()
    }
}