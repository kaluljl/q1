package com.citysimulator.game.data.supabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray

/**
 * 真实的Supabase HTTP客户端
 * 使用HTTP请求与Supabase API交互
 */
@Singleton
class RealSupabaseClient @Inject constructor() {
    
    private val gson = Gson()
    private val baseUrl = "https://bydjjbxogotpuftxzkkf.supabase.co"
    private val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ5ZGpqYnhvZ290cHVmdHh6a2tmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjExNTc2NDUsImV4cCI6MjA3NjczMzY0NX0.iSfTbt3mQnrZhIhKlFN3NymeDG779pR_tjCW1tNQkxI"
    
    /**
     * 获取所有建筑数据
     */
    suspend fun getAllBuildings(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/rest/v1/buildings")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = gson.fromJson(response, JsonArray::class.java)
                jsonArray.map { it.asJsonObject }.map { jsonObject ->
                    val map = mutableMapOf<String, Any>()
                    jsonObject.entrySet().forEach { (key, value) ->
                        map[key] = when {
                            value.isJsonPrimitive -> {
                                val primitive = value.asJsonPrimitive
                                when {
                                    primitive.isString -> primitive.asString
                                    primitive.isNumber -> primitive.asNumber
                                    primitive.isBoolean -> primitive.asBoolean
                                    else -> primitive.asString
                                }
                            }
                            else -> value.toString()
                        }
                    }
                    map
                }
            } else {
                println("获取建筑数据失败，响应码: $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            println("获取建筑数据异常: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * 插入新建筑
     */
    suspend fun insertBuilding(buildingData: Map<String, Any>): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/rest/v1/buildings")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Prefer", "return=minimal")
            connection.doOutput = true
            
            val jsonData = gson.toJson(buildingData)
            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)
            writer.write(jsonData)
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                println("建筑插入成功")
                true
            } else {
                println("建筑插入失败，响应码: $responseCode")
                false
            }
        } catch (e: Exception) {
            println("插入建筑异常: ${e.message}")
            false
        }
    }
    
    /**
     * 更新建筑
     */
    suspend fun updateBuilding(buildingId: String, buildingData: Map<String, Any>): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/rest/v1/buildings?id=eq.$buildingId")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "PATCH"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Prefer", "return=minimal")
            connection.doOutput = true
            
            val jsonData = gson.toJson(buildingData)
            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)
            writer.write(jsonData)
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                println("建筑更新成功")
                true
            } else {
                println("建筑更新失败，响应码: $responseCode")
                false
            }
        } catch (e: Exception) {
            println("更新建筑异常: ${e.message}")
            false
        }
    }
    
    /**
     * 删除建筑
     */
    suspend fun deleteBuilding(buildingId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/rest/v1/buildings?id=eq.$buildingId")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Prefer", "return=minimal")
            
            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                println("建筑删除成功")
                true
            } else {
                println("建筑删除失败，响应码: $responseCode")
                false
            }
        } catch (e: Exception) {
            println("删除建筑异常: ${e.message}")
            false
        }
    }
    
    /**
     * 测试连接
     */
    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            // 首先测试基本连接
            val url = URL("$baseUrl/rest/v1/")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            
            val responseCode = connection.responseCode
            println("Supabase连接测试 - 响应码: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 尝试测试buildings表是否存在
                try {
                    val buildingsUrl = URL("$baseUrl/rest/v1/buildings?select=id&limit=1")
                    val buildingsConnection = buildingsUrl.openConnection() as HttpURLConnection
                    
                    buildingsConnection.requestMethod = "GET"
                    buildingsConnection.setRequestProperty("apikey", apiKey)
                    buildingsConnection.setRequestProperty("Authorization", "Bearer $apiKey")
                    buildingsConnection.setRequestProperty("Content-Type", "application/json")
                    
                    val buildingsResponseCode = buildingsConnection.responseCode
                    println("Buildings表测试 - 响应码: $buildingsResponseCode")
                    
                    if (buildingsResponseCode == HttpURLConnection.HTTP_OK) {
                        println("✅ Supabase连接成功，buildings表存在")
                        true
                    } else {
                        println("❌ Buildings表不存在，需要创建数据库表")
                        false
                    }
                } catch (e: Exception) {
                    println("❌ 测试buildings表时出错: ${e.message}")
                    false
                }
            } else {
                println("❌ Supabase连接失败，响应码: $responseCode")
                false
            }
        } catch (e: Exception) {
            println("❌ 测试连接异常: ${e.message}")
            false
        }
    }
}
