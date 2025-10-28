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
import androidx.hilt.navigation.compose.hiltViewModel
import com.citysimulator.game.ui.viewmodel.SupabaseGameViewModel

/**
 * Supabase配置屏幕
 * 用于配置Supabase连接和测试数据同步
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseConfigScreen(
    onNavigateBack: () -> Unit,
    viewModel: SupabaseGameViewModel = hiltViewModel()
) {
    var supabaseUrl by remember { mutableStateOf("https://bydjjbxogotpuftxzkkf.supabase.co") }
    var supabaseKey by remember { mutableStateOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ5ZGpqYnhvZ290cHVmdHh6a2tmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjExNTc2NDUsImV4cCI6MjA3NjczMzY0NX0.iSfTbt3mQnrZhIhKlFN3NymeDG779pR_tjCW1tNQkxI") }
    var isKeyVisible by remember { mutableStateOf(false) }
    var showTestResults by remember { mutableStateOf(false) }
    
    val buildings by viewModel.buildings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
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
                text = "🗄️ Supabase配置",
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
                    text = "🌐 Supabase云数据库",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CityBlue
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "配置Supabase连接以实现云端数据同步：",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• 云端数据存储\n• 实时数据同步\n• 多设备数据共享\n• 数据备份和恢复",
                    style = MaterialTheme.typography.bodySmall,
                    color = CityOnSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Supabase URL输入
        OutlinedTextField(
            value = supabaseUrl,
            onValueChange = { supabaseUrl = it },
            label = { Text("Supabase URL") },
            placeholder = { Text("https://your-project-id.supabase.co") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Supabase Key输入
        OutlinedTextField(
            value = supabaseKey,
            onValueChange = { supabaseKey = it },
            label = { Text("Supabase Anon Key") },
            placeholder = { Text("your-anon-key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            trailingIcon = {
                IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                    Icon(
                        imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isKeyVisible) "隐藏密钥" else "显示密钥"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.testConnection()
                    showTestResults = true
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("测试连接")
            }
            
            OutlinedButton(
                onClick = {
                    // TODO: 保存配置到SharedPreferences
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("保存配置")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 数据统计
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (buildings.isNotEmpty()) 
                    CityGreen.copy(alpha = 0.1f) 
                else 
                    CityGray.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (buildings.isNotEmpty()) "✅ 连接成功" else "❌ 未连接",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (buildings.isNotEmpty()) CityGreen else CityGray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "云端建筑数量: ${buildings.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (buildings.isNotEmpty()) {
                    Text(
                        text = "数据同步正常，您的城市数据已安全存储在云端。",
                        style = MaterialTheme.typography.bodySmall,
                        color = CityOnSurfaceVariant
                    )
                } else {
                    Text(
                        text = "请配置正确的Supabase连接信息。",
                        style = MaterialTheme.typography.bodySmall,
                        color = CityOnSurfaceVariant
                    )
                }
            }
        }
        
        // 错误消息
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CityRed.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "❌ $errorMessage",
                    modifier = Modifier.padding(16.dp),
                    color = CityRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 测试结果
        if (showTestResults && buildings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CityGreen.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎉 连接测试成功！",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CityGreen
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "成功从Supabase加载了 ${buildings.size} 个建筑。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "您的城市数据现在已与云端同步！",
                        style = MaterialTheme.typography.bodySmall,
                        color = CityOnSurfaceVariant
                    )
                }
            }
        }
    }
}
