package com.citysimulator.game.data.supabase

import com.citysimulator.game.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 模拟Supabase配置类
 * 用于演示Supabase集成，实际使用时需要配置真实的Supabase项目
 */
@Singleton
class SupabaseConfig @Inject constructor() {
    
    companion object {
        // 从 BuildConfig 注入（来源于 local.properties），客户端仅应使用 URL 与 ANON KEY
        val SUPABASE_URL: String get() = BuildConfig.SUPABASE_URL
        val SUPABASE_ANON_KEY: String get() = BuildConfig.SUPABASE_ANON_KEY
        // 切勿在客户端保留 Service Role Key
    }
    
    /**
     * 模拟Supabase客户端
     * 实际使用时需要替换为真实的Supabase客户端
     */
    val supabaseClient = MockSupabaseClient()
    
    /**
     * 模拟Supabase客户端类
     */
    class MockSupabaseClient {
        fun from(table: String) = MockTable(table)
    }
    
    /**
     * 模拟表操作类
     */
    class MockTable(private val tableName: String) {
        fun select() = this
        fun insert(data: Any) = this
        fun update(data: Any) = this
        fun delete() = this
        fun filter(block: () -> Unit) = this
        fun eq(column: String, value: Any) = this
        fun <T> decodeList(): List<T> = emptyList()
        fun <T> decodeSingle(): T? = null
        fun subscribe(block: () -> Unit) = this
    }
}
