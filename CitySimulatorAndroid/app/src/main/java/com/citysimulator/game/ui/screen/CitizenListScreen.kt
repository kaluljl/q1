package com.citysimulator.game.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.citysimulator.game.data.model.*
import com.citysimulator.game.ui.theme.*

/**
 * 市民列表屏幕
 * 
 * 显示所有市民的列表，可以筛选和搜索
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenListScreen(
    citizens: List<Citizen>,
    onCitizenClick: (Citizen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterActivity by remember { mutableStateOf<CitizenActivity?>(null) }
    
    val filteredCitizens = remember(citizens, searchQuery, filterActivity) {
        citizens.filter { citizen ->
            val matchesSearch = citizen.name.contains(searchQuery, ignoreCase = true) ||
                                (citizen.occupation?.contains(searchQuery, ignoreCase = true) == true)
            val matchesActivity = filterActivity == null || citizen.currentActivity == filterActivity
            matchesSearch && matchesActivity
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("市民列表 (${citizens.size}人)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 排序 */ }) {
                        Icon(Icons.Default.Sort, "排序")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CityBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // 筛选栏
            FilterChips(
                selectedActivity = filterActivity,
                onActivitySelected = { filterActivity = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 市民列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCitizens) { citizen ->
                    CitizenListItem(
                        citizen = citizen,
                        onClick = { onCitizenClick(citizen) }
                    )
                }
                
                if (filteredCitizens.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "未找到符合条件的市民",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 搜索栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("搜索市民...") },
        leadingIcon = {
            Icon(Icons.Default.Search, "搜索")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "清除")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * 筛选标签
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    selectedActivity: CitizenActivity?,
    onActivitySelected: (CitizenActivity?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedActivity == null,
            onClick = { onActivitySelected(null) },
            label = { Text("全部") },
            leadingIcon = if (selectedActivity == null) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
        
        FilterChip(
            selected = selectedActivity == CitizenActivity.WORKING,
            onClick = { onActivitySelected(CitizenActivity.WORKING) },
            label = { Text("工作中") },
            leadingIcon = if (selectedActivity == CitizenActivity.WORKING) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
        
        FilterChip(
            selected = selectedActivity == CitizenActivity.AT_HOME,
            onClick = { onActivitySelected(CitizenActivity.AT_HOME) },
            label = { Text("在家") },
            leadingIcon = if (selectedActivity == CitizenActivity.AT_HOME) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            } else null
        )
    }
}

/**
 * 市民列表项
 */
@Composable
private fun CitizenListItem(
    citizen: Citizen,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (citizen.gender) {
                            Gender.MALE -> Color(0xFF2196F3)
                            Gender.FEMALE -> Color(0xFFE91E63)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (citizen.gender) {
                        Gender.MALE -> "👨"
                        Gender.FEMALE -> "👩"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = citizen.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${citizen.age}岁 · ${citizen.occupation ?: "无业"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 状态标签
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(
                        text = getActivityEmoji(citizen.currentActivity),
                        color = getActivityColor(citizen.currentActivity)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // 幸福度
                    val happinessEmoji = when {
                        citizen.happiness > 0.7f -> "😄"
                        citizen.happiness > 0.4f -> "🙂"
                        else -> "😟"
                    }
                    Text(
                        text = happinessEmoji,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // 箭头
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

/**
 * 状态标签
 */
@Composable
private fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

// 辅助函数
private fun getActivityEmoji(activity: CitizenActivity) = when (activity) {
    CitizenActivity.SLEEPING -> "😴"
    CitizenActivity.AT_HOME -> "🏠"
    CitizenActivity.COMMUTING_TO_WORK, CitizenActivity.COMMUTING_HOME -> "🚶"
    CitizenActivity.WORKING -> "💼"
    CitizenActivity.SHOPPING -> "🛒"
    CitizenActivity.ENTERTAINMENT -> "🎉"
    CitizenActivity.DINING -> "🍽️"
    CitizenActivity.MEDICAL -> "🏥"
    CitizenActivity.SCHOOL -> "📚"
    CitizenActivity.PARK, CitizenActivity.EXERCISING -> "🌳"
    CitizenActivity.SOCIALIZING -> "👥"
}

private fun getActivityColor(activity: CitizenActivity) = when (activity) {
    CitizenActivity.SLEEPING -> Color(0xFF9C27B0)
    CitizenActivity.AT_HOME -> Color(0xFF4CAF50)
    CitizenActivity.WORKING -> Color(0xFF2196F3)
    CitizenActivity.COMMUTING_TO_WORK, CitizenActivity.COMMUTING_HOME -> Color(0xFFFF9800)
    CitizenActivity.ENTERTAINMENT, CitizenActivity.SOCIALIZING -> Color(0xFFE91E63)
    else -> Color(0xFF607D8B)
}

