package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Citizen

/**
 * 市民情绪气泡组件
 * 
 * 显示市民当前的情绪和想法
 * 
 * @author AI进化论-花生
 */
@Composable
fun CitizenEmotionBubble(
    citizen: Citizen,
    modifier: Modifier = Modifier
) {
    val emotion = getEmotionFromHappiness(citizen.happiness)
    val emoji = getEmotionEmoji(emotion)
    
    // 动画效果
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size((24 * scale).dp)
            .background(
                color = getEmotionColor(emotion).copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (14 * scale).sp
        )
    }
}

/**
 * 思维气泡组件（显示市民的想法）
 */
@Composable
fun CitizenThoughtBubble(
    thought: String,
    emotion: CitizenEmotion,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    
    // 自动消失动画
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000) // 3秒后消失
        visible = false
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "thought_alpha"
    )
    
    if (visible) {
        Box(
            modifier = modifier
                .alpha(alpha)
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = getEmotionEmoji(emotion),
                    fontSize = 16.sp
                )
                Text(
                    text = thought,
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * 活动指示器（显示市民正在做什么）
 */
@Composable
fun CitizenActivityIndicator(
    activity: com.citysimulator.game.data.model.CitizenActivity,
    modifier: Modifier = Modifier
) {
    val activityEmoji = getActivityEmoji(activity)
    val activityColor = getActivityColor(activity)
    
    Box(
        modifier = modifier
            .size(20.dp)
            .background(
                color = activityColor.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = activityEmoji,
            fontSize = 12.sp
        )
    }
}

/**
 * 市民情绪枚举
 */
enum class CitizenEmotion {
    VERY_HAPPY,
    HAPPY,
    CONTENT,
    NEUTRAL,
    UNHAPPY,
    SAD,
    ANGRY;
    
    fun getDisplayName(): String = when (this) {
        VERY_HAPPY -> "非常开心"
        HAPPY -> "开心"
        CONTENT -> "满足"
        NEUTRAL -> "平静"
        UNHAPPY -> "不开心"
        SAD -> "难过"
        ANGRY -> "愤怒"
    }
}

/**
 * 根据幸福度获取情绪
 */
private fun getEmotionFromHappiness(happiness: Float): CitizenEmotion {
    return when {
        happiness > 0.9f -> CitizenEmotion.VERY_HAPPY
        happiness > 0.7f -> CitizenEmotion.HAPPY
        happiness > 0.5f -> CitizenEmotion.CONTENT
        happiness > 0.4f -> CitizenEmotion.NEUTRAL
        happiness > 0.25f -> CitizenEmotion.UNHAPPY
        happiness > 0.1f -> CitizenEmotion.SAD
        else -> CitizenEmotion.ANGRY
    }
}

/**
 * 获取情绪对应的emoji
 */
private fun getEmotionEmoji(emotion: CitizenEmotion): String {
    return when (emotion) {
        CitizenEmotion.VERY_HAPPY -> "😄"
        CitizenEmotion.HAPPY -> "😊"
        CitizenEmotion.CONTENT -> "🙂"
        CitizenEmotion.NEUTRAL -> "😐"
        CitizenEmotion.UNHAPPY -> "😕"
        CitizenEmotion.SAD -> "😢"
        CitizenEmotion.ANGRY -> "😠"
    }
}

/**
 * 获取情绪对应的颜色
 */
private fun getEmotionColor(emotion: CitizenEmotion): Color {
    return when (emotion) {
        CitizenEmotion.VERY_HAPPY -> Color(0xFF4CAF50)
        CitizenEmotion.HAPPY -> Color(0xFF8BC34A)
        CitizenEmotion.CONTENT -> Color(0xFFCDDC39)
        CitizenEmotion.NEUTRAL -> Color(0xFFFFC107)
        CitizenEmotion.UNHAPPY -> Color(0xFFFF9800)
        CitizenEmotion.SAD -> Color(0xFFFF5722)
        CitizenEmotion.ANGRY -> Color(0xFFF44336)
    }
}

/**
 * 获取活动对应的emoji
 */
private fun getActivityEmoji(activity: com.citysimulator.game.data.model.CitizenActivity): String {
    return when (activity) {
        com.citysimulator.game.data.model.CitizenActivity.SLEEPING -> "💤"
        com.citysimulator.game.data.model.CitizenActivity.AT_HOME -> "🏠"
        com.citysimulator.game.data.model.CitizenActivity.COMMUTING_TO_WORK,
        com.citysimulator.game.data.model.CitizenActivity.COMMUTING_HOME -> "🚶"
        com.citysimulator.game.data.model.CitizenActivity.WORKING -> "💼"
        com.citysimulator.game.data.model.CitizenActivity.SHOPPING -> "🛒"
        com.citysimulator.game.data.model.CitizenActivity.ENTERTAINMENT -> "🎮"
        com.citysimulator.game.data.model.CitizenActivity.DINING -> "🍽️"
        com.citysimulator.game.data.model.CitizenActivity.MEDICAL -> "🏥"
        com.citysimulator.game.data.model.CitizenActivity.SCHOOL -> "📚"
        com.citysimulator.game.data.model.CitizenActivity.PARK -> "🌳"
        com.citysimulator.game.data.model.CitizenActivity.EXERCISING -> "🏃"
        com.citysimulator.game.data.model.CitizenActivity.SOCIALIZING -> "👥"
    }
}

/**
 * 获取活动对应的颜色
 */
private fun getActivityColor(activity: com.citysimulator.game.data.model.CitizenActivity): Color {
    return when (activity) {
        com.citysimulator.game.data.model.CitizenActivity.SLEEPING -> Color(0xFF5E35B1)
        com.citysimulator.game.data.model.CitizenActivity.AT_HOME -> Color(0xFF1976D2)
        com.citysimulator.game.data.model.CitizenActivity.WORKING -> Color(0xFF388E3C)
        com.citysimulator.game.data.model.CitizenActivity.SHOPPING -> Color(0xFFFFA000)
        com.citysimulator.game.data.model.CitizenActivity.ENTERTAINMENT -> Color(0xFFE91E63)
        com.citysimulator.game.data.model.CitizenActivity.PARK -> Color(0xFF66BB6A)
        com.citysimulator.game.data.model.CitizenActivity.EXERCISING -> Color(0xFFFF5722)
        else -> Color(0xFF757575)
    }
}

