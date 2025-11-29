package com.temi.conversationalrobot.data.models

data class MenuItem(
    val name: String,
    val price: Double,
    val description: String,
    val ingredients: String,
    val allergens: String,
    val category: String
)

data class MenuData(
    val items: List<MenuItem>
)

