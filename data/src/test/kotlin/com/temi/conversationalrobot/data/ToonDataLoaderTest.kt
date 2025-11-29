package com.temi.conversationalrobot.data

import android.content.Context
import android.content.res.AssetManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class ToonDataLoaderTest {
    
    private lateinit var mockContext: Context
    private lateinit var mockAssetManager: AssetManager
    private lateinit var toonDataLoader: ToonDataLoader
    
    @Before
    fun setup() {
        mockContext = mockk<Context>(relaxed = true)
        mockAssetManager = mockk<AssetManager>(relaxed = true)
        every { mockContext.assets } returns mockAssetManager
        toonDataLoader = ToonDataLoader(mockContext)
    }
    
    @Test
    fun `test successful parsing of menu toon file`() {
        val menuContent = """menu[2]{name,price,description,ingredients,allergens,category}:
burger,12.99,Classic beef burger,beef patty lettuce bun,gluten dairy,entrees
salad,9.99,Fresh salad,lettuce tomato vinaigrette,none,salads"""
        
        val inputStream = ByteArrayInputStream(menuContent.toByteArray())
        every { mockAssetManager.open("menu.toon") } returns inputStream
        
        val menuData = toonDataLoader.loadMenuData()
        
        assertNotNull(menuData)
        assertEquals(2, menuData.items.size)
        assertEquals("burger", menuData.items[0].name)
        assertEquals(12.99, menuData.items[0].price, 0.01)
        assertEquals("salad", menuData.items[1].name)
    }
    
    @Test
    fun `test successful parsing of knowledge base toon file`() {
        val kbContent = """restaurant_info:
  name:Test Restaurant
  location:123 Main St
  phone:555-0123
  email:test@restaurant.com
  hours:
    monday:11:00-22:00
  policies:
    parking:Free parking
faq[1]{question,answer}:
What are your hours?,We are open 11am-10pm"""
        
        val inputStream = ByteArrayInputStream(kbContent.toByteArray())
        every { mockAssetManager.open("knowledge-base.toon") } returns inputStream
        
        val kbData = toonDataLoader.loadKnowledgeBaseData()
        
        assertNotNull(kbData)
        assertEquals("Test Restaurant", kbData.restaurantInfo.name)
        assertEquals("123 Main St", kbData.restaurantInfo.location)
        assertEquals(1, kbData.faq.size)
        assertEquals("What are your hours?", kbData.faq[0].question)
    }
    
    @Test(expected = ToonParseException::class)
    fun `test error handling for malformed TOON syntax`() {
        val malformedContent = "invalid toon syntax {["
        val inputStream = ByteArrayInputStream(malformedContent.toByteArray())
        every { mockAssetManager.open("menu.toon") } returns inputStream
        
        toonDataLoader.loadMenuData()
    }
    
    @Test
    fun `test data structure validation expected fields present`() {
        val menuContent = """menu[1]{name,price,description,ingredients,allergens,category}:
burger,12.99,Classic burger,beef bun,gluten,entrees"""
        
        val inputStream = ByteArrayInputStream(menuContent.toByteArray())
        every { mockAssetManager.open("menu.toon") } returns inputStream
        
        val menuData = toonDataLoader.loadMenuData()
        val item = menuData.items[0]
        
        assertTrue(item.name.isNotEmpty())
        assertTrue(item.price > 0)
        assertTrue(item.description.isNotEmpty())
        assertTrue(item.ingredients.isNotEmpty())
        assertNotNull(item.allergens)
        assertTrue(item.category.isNotEmpty())
    }
}

