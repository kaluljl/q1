package com.citysimulator.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import android.view.View

/**
 * 启动加载页面
 * 
 * 功能：
 * - 显示应用图标和名称
 * - 显示加载动画
 * - 延迟2秒后自动跳转到主页面
 */
@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit
) {
    // 设置系统栏为透明
    val view = LocalView.current
    LaunchedEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            it.statusBarColor = android.graphics.Color.TRANSPARENT
            it.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }
    
    // 动画状态
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    // Logo 缩放动画
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )
    
    // 渐显动画
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "alpha"
    )
    
    // 加载进度动画
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    
    // 延迟后跳转
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToMain()
    }
    
    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF2196F3)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha)
        ) {
            // Logo/Icon
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = "App Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 应用名称
            Text(
                text = "城市模拟器",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 副标题
            Text(
                text = "City Build Simulator",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 加载进度条
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(40.dp),
                color = Color.White,
                strokeWidth = 3.dp,
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 加载文字
            Text(
                text = "正在加载...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        
        // 底部版本信息
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Build Your Dream City",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Version 1.0.0",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

