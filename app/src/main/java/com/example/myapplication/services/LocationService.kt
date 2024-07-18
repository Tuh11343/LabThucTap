package com.example.myapplication.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.myapplication.model.lab7.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var geoList = mutableListOf<GeoLocation>()
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    companion object {
        var isRunning = false
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "GoogleMapChannel"

        private val PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
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

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        createLocationCallback()
        createHandler()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("DEBUG", "Location Service hoạt động")
        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification())
        requestLocationUpdates()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyHandle()
    }

    //Hàm này dùng để xử lý khi loại bỏ các thành phần cuủa service
    private fun onDestroyHandle() {
        isRunning = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handler.removeCallbacks(runnable)
        Log.i("DEBUG", "Dừng location service")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        onDestroyHandle()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    /*--------------------------- Phần này xử lý tạo thông báo -----------------------------------*/
    private fun createNotification(): Notification {
        createNotificationChannel()

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Google Map")
            .setContentText("Running")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Foreground Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
    /*--------------------------------------------------------------------------------------------*/

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    }

    private fun createHandler() {
        handler = Handler(mainLooper)

        //Tạo vòng lặp chạy mỗi 30s
        runnable = object : Runnable {
            override fun run() {
                if (checkPermission()) {
                    if (isLocationEnabled()) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            null,
                        )
                    } else {
                        Log.i("DEBUG", "Vị trí không được bật vui lòng kiểm tra")
                    }
                    Log.i("DEBUG", "Cap nhat moi 30s")
                    handler.postDelayed(this, 30000)
                }
            }
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->

                    Log.i(
                        "DEBUG",
                        "Vi tri trong receiver: ${location.latitude}, ${location.longitude}"
                    )

                    //Gửi dữ liệu lên cho activity
                    val intent = Intent("LOCATION_UPDATE")
                    intent.putExtra("latitude", location.latitude)
                    intent.putExtra("longitude", location.longitude)
                    sendBroadcast(intent)

                    //Lấy địa chỉ
                    convertLatLongToAddress(
                        this@LocationService, location.latitude, location.longitude
                    ) {
                        Log.i("DEBUG", "Địa chỉ:$it")

                        //Tính toán khoảng cách
                        if (geoList.isEmpty()) {
                            it?.apply {
                                Log.i("DEBUG", "Khởi tạo địa chỉ\n")
                                geoList.add(GeoLocation(it, location.latitude, location.longitude))
                            }
                        } else {
                            val geoLocation = geoList.last()
                            if (calculateDistance(
                                    geoLocation.latitude,
                                    geoLocation.longitude,
                                    location.latitude,
                                    location.longitude
                                )
                            ) {
                                it?.apply {
                                    Log.i("DEBUG", "Thêm địa chỉ mới\n")
                                    geoList.add(
                                        GeoLocation(
                                            it,
                                            location.latitude,
                                            location.longitude
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /*//Hàm này dùng để kiểm tra xem có bật GPS chưa nếu chưa yêu cầu bật
    private fun requestLocationEnable() {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {

                    //Gọi broadcast tới activity để yêu cầu bật vị trí
                    val intent = Intent("REQUEST_LOCATION_ENABLE")
                    sendBroadcast(intent)

                } catch (e: Exception) {
                    Log.e("DEBUG", "Error:$e")
                }
            }
        }
        task.addOnSuccessListener {
            Log.i("DEBUG", "GPS da bat")

        }

    }*/

    //Hàm này dùng để gọi lấy vị trí
    private fun requestLocationUpdates() {

        if (!checkPermission()) {
            Log.i("DEBUG", "Quyền chưa được cấp")
            stopSelf()
        } else {
            handler.post(runnable)
        }

    }

    //Hàm này dùng để chuyển đổi vĩ độ,kinh độ ra thành địa chỉ
    private fun convertLatLongToAddress(
        context: Context,
        latitude: Double,
        longitude: Double,
        callback: (String?) -> Unit
    ) {
        val geocoder = Geocoder(context)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isEmpty()) {
                            callback(null)
                        } else {

                            /*val address = it[0].getAddressLine(0)
                            val city = it[0].locality
                            val state = it[0].adminArea
                            val country = it[0].countryName
                            val postalCode = it[0].postalCode
                            val knownName = it[0].featureName*/
                            val address = addresses[0].getAddressLine(0)
                            callback(address)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        Log.i("DEBUG", "Loi geoCoder listener:$errorMessage")
                    }
                })
            } else {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.get(0)?.getAddressLine(0)
                callback(address)

            }
        } catch (e: IOException) {
            callback(null)
        }
    }

    //Hàm này dùng để tính toán khoảng cách giữa 2 vị trí
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Boolean {
        val startLocation = Location("startLocation").apply {
            latitude = lat1
            longitude = lon1
        }

        val endLocation = Location("endLocation").apply {
            latitude = lat2
            longitude = lon2
        }

        val distance = startLocation.distanceTo(endLocation)
        return if (distance > 20) {
            Log.i("DEBUG", "Khoang cach:$distance")
            true
        } else {
            Log.i("DEBUG", "Khoang cach:$distance")
            false
        }
    }
}