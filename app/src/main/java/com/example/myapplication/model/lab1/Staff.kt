package com.example.myapplication

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class Staff(var id:Int?, var name:String) {

    constructor():this(null,"")

    companion object{
        private val gson = Gson()

        fun getStaffList(jsonElement: JsonElement): MutableList<Staff> {
            val jsonObject = jsonElement.asJsonObject
            val contents = jsonObject.getAsJsonArray("contents")
            val mcpJsonArray: JsonArray = contents.get(0).asJsonObject.getAsJsonArray("mcpList")

            var staffList= mutableListOf<Staff>()

            for (mcpElement in mcpJsonArray) {
                val mcpObject = mcpElement.asJsonObject
                val id = mcpObject.get("id").asInt
                val name = mcpObject.get("name").asString
                staffList.add(Staff(id,name))
            }

            return staffList
        }

        fun getStaff(jsonElement: JsonElement): Staff? {
            val jsonObject = jsonElement.asJsonObject
            if (jsonObject.has("account")) {
                val account: JsonObject = jsonObject.getAsJsonObject("account")
                val type = object : TypeToken<Staff>() {}.type
                return gson.fromJson(account, type)
            }
            return null
        }

        fun getLength(jsonElement: JsonElement):Int{
            return jsonElement.asJsonObject.get("length").asInt
        }

        fun getStatus(jsonElement: JsonElement):String{
            return jsonElement.asJsonObject.get("status").asString
        }

    }
}