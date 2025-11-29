package com.temi.conversationalrobot.data.repository

import com.temi.conversationalrobot.data.ToonDataLoader
import com.temi.conversationalrobot.data.models.KnowledgeBaseData
import com.temi.conversationalrobot.data.models.MenuData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ToonRepository {
    fun getMenuData(): Flow<Result<MenuData>>
    fun getKnowledgeBaseData(): Flow<Result<KnowledgeBaseData>>
}

class ToonRepositoryImpl(
    private val toonDataLoader: ToonDataLoader
) : ToonRepository {
    
    override fun getMenuData(): Flow<Result<MenuData>> = flow {
        try {
            val menuData = toonDataLoader.loadMenuData()
            emit(Result.success(menuData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getKnowledgeBaseData(): Flow<Result<KnowledgeBaseData>> = flow {
        try {
            val kbData = toonDataLoader.loadKnowledgeBaseData()
            emit(Result.success(kbData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

