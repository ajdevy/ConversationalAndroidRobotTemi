package com.temi.conversationalrobot.data.models

data class RestaurantInfo(
    val name: String,
    val location: String,
    val phone: String,
    val email: String,
    val hours: Map<String, String>,
    val policies: Map<String, String>
)

data class FaqEntry(
    val question: String,
    val answer: String
)

data class KnowledgeBaseData(
    val restaurantInfo: RestaurantInfo,
    val faq: List<FaqEntry>
)

