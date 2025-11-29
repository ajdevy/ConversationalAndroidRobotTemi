package com.temi.conversationalrobot.data

import android.content.Context
import br.com.vexpera.ktoon.Toon
import com.temi.conversationalrobot.data.models.KnowledgeBaseData
import com.temi.conversationalrobot.data.models.MenuData
import com.temi.conversationalrobot.data.models.MenuItem
import com.temi.conversationalrobot.data.models.RestaurantInfo
import com.temi.conversationalrobot.data.models.FaqEntry

class ToonDataLoader(private val context: Context) {
    
    private var cachedMenuData: MenuData? = null
    private var cachedKnowledgeBaseData: KnowledgeBaseData? = null
    
    fun loadMenuData(): MenuData {
        if (cachedMenuData != null) {
            return cachedMenuData!!
        }
        
        try {
            val menuText = context.assets.open("menu.toon").bufferedReader().use { it.readText() }
            @Suppress("UNCHECKED_CAST")
            val decoded = Toon.decode(menuText) as? Map<String, Any?> ?: emptyMap()
            
            @Suppress("UNCHECKED_CAST")
            val menuArray = decoded["menu"] as? List<Map<String, Any>> ?: emptyList()
            
            val menuItems = menuArray.mapNotNull { item ->
                try {
                    MenuItem(
                        name = (item["name"] as? String) ?: "",
                        price = when (val priceValue = item["price"]) {
                            is String -> priceValue.toDoubleOrNull() ?: 0.0
                            is Number -> priceValue.toDouble()
                            else -> 0.0
                        },
                        description = (item["description"] as? String) ?: "",
                        ingredients = (item["ingredients"] as? String) ?: "",
                        allergens = (item["allergens"] as? String) ?: "",
                        category = (item["category"] as? String) ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            if (menuItems.isEmpty()) {
                throw ToonParseException("No valid menu items found in menu.toon")
            }
            
            cachedMenuData = MenuData(items = menuItems)
            return cachedMenuData!!
        } catch (e: ToonParseException) {
            throw e
        } catch (e: Exception) {
            throw ToonParseException("Failed to parse menu.toon: ${e.message}", e)
        }
    }
    
    fun loadKnowledgeBaseData(): KnowledgeBaseData {
        if (cachedKnowledgeBaseData != null) {
            return cachedKnowledgeBaseData!!
        }
        
        try {
            val kbText = context.assets.open("knowledge-base.toon").bufferedReader().use { it.readText() }
            @Suppress("UNCHECKED_CAST")
            val decoded = Toon.decode(kbText) as? Map<String, Any?> ?: emptyMap()
            
            @Suppress("UNCHECKED_CAST")
            val restaurantInfoMap = decoded["restaurant_info"] as? Map<String, Any> ?: emptyMap()
            
            @Suppress("UNCHECKED_CAST")
            val hoursMap = (restaurantInfoMap["hours"] as? Map<String, String>) ?: emptyMap()
            
            @Suppress("UNCHECKED_CAST")
            val policiesMap = (restaurantInfoMap["policies"] as? Map<String, String>) ?: emptyMap()
            
            val restaurantInfo = RestaurantInfo(
                name = (restaurantInfoMap["name"] as? String) ?: "",
                location = (restaurantInfoMap["location"] as? String) ?: "",
                phone = (restaurantInfoMap["phone"] as? String) ?: "",
                email = (restaurantInfoMap["email"] as? String) ?: "",
                hours = hoursMap,
                policies = policiesMap
            )
            
            @Suppress("UNCHECKED_CAST")
            val faqArray = decoded["faq"] as? List<Map<String, String>> ?: emptyList()
            
            val faqList = faqArray.mapNotNull { entry ->
                try {
                    FaqEntry(
                        question = entry["question"] ?: "",
                        answer = entry["answer"] ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            cachedKnowledgeBaseData = KnowledgeBaseData(
                restaurantInfo = restaurantInfo,
                faq = faqList
            )
            return cachedKnowledgeBaseData!!
        } catch (e: ToonParseException) {
            throw e
        } catch (e: Exception) {
            throw ToonParseException("Failed to parse knowledge-base.toon: ${e.message}", e)
        }
    }
}

class ToonParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

