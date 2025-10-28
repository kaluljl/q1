package com.citysimulator.game.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.citysimulator.game.ui.theme.*
import com.citysimulator.game.utils.APIKeyManager
import javax.inject.Inject

/**
 * API密钥配置屏幕
 * 用于设置DeepSeek API密钥
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIKeyConfigScreen(
    onNavigateBack: () -> Unit,
    apiKeyManager: APIKeyManager
) {
    var apiKey by remember { mutableStateOf(apiKeyManager.getDeepSeekApiKey()) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔑 API密钥配置",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = onNavigateBack) {
                Text("← 返回")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 说明文本
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CityBlue.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🤖 DeepSeek AI配置",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CityBlue
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "请输入您的DeepSeek API密钥以启用AI功能：",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• 城市智能分析\n• NPC智能对话\n• 发展预测\n• 建筑推荐",
                    style = MaterialTheme.typography.bodySmall,
                    color = CityOnSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // API密钥输入
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("DeepSeek API密钥") },
            placeholder = { Text("sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "隐藏密钥" else "显示密钥"
                    )
                }
            },
            isError = apiKey.isNotEmpty() && !apiKey.startsWith("sk-")
        )
        
        if (apiKey.isNotEmpty() && !apiKey.startsWith("sk-")) {
            Text(
                text = "API密钥格式不正确，应以'sk-'开头",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (apiKey.isNotEmpty() && apiKey.startsWith("sk-")) {
                        apiKeyManager.setDeepSeekApiKey(apiKey)
                        showSuccessMessage = true
                    } else {
                        showErrorMessage = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = apiKey.isNotEmpty()
            ) {
                Text("保存密钥")
            }
            
            OutlinedButton(
                onClick = {
                    apiKey = ""
                    apiKeyManager.clearApiKey()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("清除")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 测试连接按钮
        Button(
            onClick = {
                // 这里可以添加测试API连接的功能
                showSuccessMessage = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = apiKeyManager.hasValidApiKey()
        ) {
            Text("测试AI连接")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 状态信息
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (apiKeyManager.hasValidApiKey()) 
                    CityGreen.copy(alpha = 0.1f) 
                else 
                    CityGray.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (apiKeyManager.hasValidApiKey()) "✅ AI功能已启用" else "❌ AI功能未启用",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (apiKeyManager.hasValidApiKey()) CityGreen else CityGray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (apiKeyManager.hasValidApiKey()) 
                        "您可以享受完整的AI功能，包括智能对话、城市分析和预测。" 
                    else 
                        "请配置有效的API密钥以启用AI功能。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 成功消息
        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CityGreen.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "✅ API密钥已保存！AI功能已启用。",
                    modifier = Modifier.padding(16.dp),
                    color = CityGreen,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 错误消息
        if (showErrorMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showErrorMessage = false
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CityRed.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "❌ 请检查API密钥格式是否正确。",
                    modifier = Modifier.padding(16.dp),
                    color = CityRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
