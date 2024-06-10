package com.example.myapplication

data class RegisterRequest(
    val contents: RegisterContents
)

data class RegisterContents(
    val username: String,
    val password: String,
    val imei: String,
    val subscriberId: String,
    val appVersion: String,
    val deviceName: String
)