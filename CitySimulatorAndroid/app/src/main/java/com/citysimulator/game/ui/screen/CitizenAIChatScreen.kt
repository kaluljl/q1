package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.citysimulator.game.data.model.Citizen
import com.citysimulator.game.ui.theme.CityBlue
import com.citysimulator.game.ui.viewmodel.AIDialogueViewModel
import kotlinx.coroutines.launch

/**
 * 市民AI对话屏幕
 * 
 * 使用DeepSeek AI与市民进行对话
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenAIChatScreen(
    citizen: Citizen,
    onBack: () -> Unit,
    viewModel: AIDialogueViewModel = hiltViewModel(),
    citizenViewModel: com.citysimulator.game.ui.viewmodel.CitizenViewModel = hiltViewModel()
) {
    var userInput by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // 设置CitizenViewModel（只设置一次）
    LaunchedEffect(Unit) {
        viewModel.setCitizenViewModel(citizenViewModel)
    }
    
    // 初始化对话
    LaunchedEffect(citizen.id) {
        viewModel.startConversationWithCitizen(citizen)
    }
    
    // 自动滚动到最新消息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("与 ${citizen.name} 对话")
                        Text(
                            text = "${citizen.occupation} · ${citizen.age}岁",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CityBlue
                )
            )
        },
        bottomBar = {
            // 输入框
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("输入消息...") },
                        enabled = !isLoading,
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (userInput.isNotBlank()) {
                                viewModel.sendMessage(userInput)
                                userInput = ""
                            }
                        },
                        enabled = !isLoading && userInput.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送",
                            tint = if (!isLoading && userInput.isNotBlank()) 
                                CityBlue else Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(
                    message = message.content,
                    isUser = message.isUser,
                    timestamp = message.timestamp
                )
            }
            
            // 加载指示器
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${citizen.name}正在思考...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 聊天消息气泡
 */
@Composable
private fun ChatMessageBubble(
    message: String,
    isUser: Boolean,
    timestamp: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    CityBlue.copy(alpha = 0.9f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else Color.Black
                )
            }
        }
    }
}

/**
 * 聊天消息数据类
 */
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

