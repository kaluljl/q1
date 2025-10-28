package com.citysimulator.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.citysimulator.game.ui.navigation.CitySimulatorNavigation
import com.citysimulator.game.ui.theme.CitySimulatorTheme
import com.citysimulator.game.utils.APIKeyInitializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 城市模拟经营游戏主Activity
 * 
 * 这是应用的主入口点，负责初始化UI和导航系统。
 * 使用Jetpack Compose构建现代化的Material Design 3界面。
 * 
 * @author AI进化论-花生
 * @since 1.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var apiKeyInitializer: APIKeyInitializer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 初始化DeepSeek API密钥(首次启动时自动配置)
        apiKeyInitializer.initializeApiKey("sk-a025e298014e47d7baa61a07f4d4d784")
        
        try {
            setContent {
                CitySimulatorTheme {
                    // 恢复导航系统
                    CitySimulatorApp()
                }
            }
        } catch (e: Exception) {
            println("❌ MainActivity崩溃: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}

@Composable
fun TestScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "城市模拟经营游戏",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { /* TODO: 导航到建筑菜单 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("建筑菜单")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { /* TODO: 导航到资源面板 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("资源面板")
        }
    }
}

/**
 * 城市模拟经营游戏主应用组件
 * 
 * 使用Scaffold提供基础布局结构，包含导航系统。
 * 支持边缘到边缘显示，提供沉浸式体验。
 */
@Composable
fun CitySimulatorApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CitySimulatorNavigation(
                navController = navController,
                context = context
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CitySimulatorAppPreview() {
    CitySimulatorTheme {
        CitySimulatorApp()
    }
}
