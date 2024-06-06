package com.example.myapplication.request

data class SearchRequest(
    val contents: SearchContents
)

data class SearchContents(
    val mcpDay: Int,
    val userId: Int,
)