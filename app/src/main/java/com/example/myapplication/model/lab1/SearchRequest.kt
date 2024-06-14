
package com.example.myapplication.model.lab1


data class SearchRequest(
    val contents: SearchContents
)

data class SearchContents(
    val mcpDay: Int,
    val userId: Int,
)