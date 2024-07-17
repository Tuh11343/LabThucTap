package com.example.myapplication.views

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.Lab7LayoutBinding
import com.example.myapplication.services.LocationService
import com.example.myapplication.utils.lab7.StopServiceReceiver
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Calendar


class Lab7Activity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: Lab7LayoutBinding
    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationUpdateReceiver: BroadcastReceiver
    private var addMarker:Marker?=null

    //Thằng này dùng để yêu cầu người dùng bật GPS
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "GPS đã được bật", Toast.LENGTH_SHORT).show()
                startLocationService()
            } else {
                Toast.makeText(this, "Chưa bật GPS", Toast.LENGTH_SHORT).show()

            }
        }

    //List các quyền
    companion object {
        private val PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = Lab7LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setAlarmToStopService(this,20,0)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DEBUG","Xóa location update receiver!")
        unregisterReceiver(locationUpdateReceiver)
    }

    //Hàm dùng để bắt đầu tạo service
    private fun startLocationService() {
        val intent = Intent(applicationContext, LocationService::class.java)
        startForegroundService(intent)
    }


    //Hàm này được gọi khi xử lý yêu cầu truy cập quyền
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
                    // Xử lý khi đã cấp quyền

                    //Lấy địa chỉ người dùng
                    if(checkPermission()){
                        gMap.isMyLocationEnabled = true
                    }
                    setUpLocation()
                } else {
                    // Xử lý khi người dùng từ chối cấp quyền

                    Toast.makeText(this,"Không thể khởi chạy ứng dụng nếu chưa cấp quyền",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Xử lý các requestCode khác nếu có
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSION, 0)
        }else{

            //Check xem service có đang hoạt động hay không
            if(!LocationService.isRunning){
                //Lấy địa chỉ người dùng
                gMap.isMyLocationEnabled = true
                setUpLocation()
            }else{
                Log.i("DEBUG","Service đã hoạt động")

                //Đăng ký receiver cập nhật vị trí
                registerLocationUpdateReceiver()
            }
        }
    }

    /*------------------------------------ Hàm này dùng để khởi tạo ------------------------------*/
    private fun setUpLocation() {

        //Kiểm tra xem đã bật vị trí hay chưa
        requestLocationEnable()

        //Đăng ký receiver cập nhật vị trí
        registerLocationUpdateReceiver()
    }


    /*--------------- Hàm này dùng để đăng ký receiver xử lý cập nhật UI cho googleMap -----------*/
    private fun registerLocationUpdateReceiver() {

        //Khởi tạo receiver
        locationUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent != null) {
                    val latitude = intent.getDoubleExtra("latitude", 0.0)
                    val longitude = intent.getDoubleExtra("longitude", 0.0)

                    /*-------------------------------Cập nhật UI--------------------------------------*/
                    //Xóa marker cũ
                    gMap.clear()

                    //Đưa cam lại vị trí
                    val userLocation = LatLng(latitude, longitude)

                    //Xử lý đưa marker tới vị trí tìm được
                    gMap.addMarker(
                        MarkerOptions().position(userLocation).title("Vị trí của bạn")
                    )
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    Log.i(
                        "DEBUG",
                        "Tọa độ của bạn: ${latitude}, $longitude"
                    )
                } else {
                    Log.i("DEBUG", "Không tìm thấy dữ liệu gửi từ service")
                }


            }
        }

        //Đăng ký receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                locationUpdateReceiver, IntentFilter("LOCATION_UPDATE"),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(locationUpdateReceiver, IntentFilter("LOCATION_UPDATE"))
        }
    }

    /*----------------------- Hàm này dùng để xử lý yêu cầu bật vị trí ---------------------------*/
    private fun requestLocationEnable() {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resultLauncher.launch(intentSenderRequest)

                } catch (e: Exception) {
                    Log.e("DEBUG", "Error:$e")
                }
            }
        }
        task.addOnSuccessListener {
            Log.i("DEBUG", "GPS da bat")
            startLocationService()
        }

    }



    /*--------------------------------------- Phần xử lý alarm -----------------------------------*/
    private fun setAlarmToStopService(context: Context, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        try{
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, StopServiceReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }catch (e:SecurityException){
            Log.e("DEBUG","Loi xu ly alarm:$e")
        }
    }
}