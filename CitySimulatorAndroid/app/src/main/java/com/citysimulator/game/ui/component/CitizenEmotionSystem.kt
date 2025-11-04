package com.citysimulator.game.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.citysimulator.game.data.model.Citizen
import kotlinx.coroutines.delay

/**
 * 市民情绪系统 - 拟人化视觉表达
 * 
 * 功能：
 * - 情绪气泡显示（爱心、愤怒、困惑、开心等）
 * - 行为动画（跳跃、低头、挥手等）
 * - 动态情绪变化
 * 
 * @author AI进化论-花生
 */

/**
 * 情绪类型枚举
 */
enum class EmotionType {
    HAPPY,          // 开心 😊
    VERY_HAPPY,     // 非常开心 🎉
    SAD,            // 悲伤 😢
    ANGRY,          // 愤怒 😠
    LOVE,           // 爱心 ❤️
    CONFUSED,       // 困惑 ❓
    TIRED,          // 疲惫 😴
    WORKING,        // 工作中 💼
    THINKING,       // 思考 💭
    CELEBRATING,    // 庆祝 🎊
    COMPLAINING,    // 抱怨 💢
    NEUTRAL         // 中性 😐
}

/**
 * 行为动画类型
 */
enum class BehaviorAnimation {
    IDLE,           // 静止
    JUMP,           // 跳跃（开心时）
    BOW,            // 低头（悲伤时）
    WAVE,           // 挥手（社交时）
    SHAKE,          // 摇头（愤怒/抱怨时）
    BOUNCE          // 弹跳（兴奋时）
}

/**
 * 带情绪动画的市民标记
 */
@Composable
fun EmotionalCitizenMarker(
    citizen: Citizen,
    showEmotionBubble: Boolean = true,
    modifier: Modifier = Modifier
) {
    // 计算当前情绪
    val emotion = remember(citizen.happiness, citizen.complaints) {
        calculateEmotion(citizen)
    }
    
    // 计算当前行为动画
    val behavior = remember(emotion) {
        getBehaviorForEmotion(emotion)
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 情绪气泡（显示在市民上方）
        if (showEmotionBubble) {
            EmotionBubble(
                emotion = emotion,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
            )
        }
        
        // 市民本体（带行为动画）
        AnimatedCitizen(
            citizen = citizen,
            behavior = behavior
        )
    }
}

/**
 * 情绪气泡组件
 */
@Composable
private fun EmotionBubble(
    emotion: EmotionType,
    modifier: Modifier = Modifier
) {
    // 气泡出现和消失的动画
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(emotion) {
        visible = true
        delay(3000) // 显示3秒
        visible = false
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "bubble_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bubble_scale"
    )
    
    if (alpha > 0f) {
        Box(
            modifier = modifier
                .size(24.dp)
                .scale(scale)
                .alpha(alpha)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getEmotionEmoji(emotion),
                fontSize = 14.sp
            )
        }
    }
}

/**
 * 带动画的市民组件
 */
@Composable
private fun AnimatedCitizen(
    citizen: Citizen,
    behavior: BehaviorAnimation,
    modifier: Modifier = Modifier
) {
    // 跳跃动画
    val jumpOffset by rememberInfiniteFloatAnimation(
        enabled = behavior == BehaviorAnimation.JUMP,
        initialValue = 0f,
        targetValue = -8f,
        durationMillis = 400
    )
    
    // 低头动画
    val bowScale by rememberInfiniteFloatAnimation(
        enabled = behavior == BehaviorAnimation.BOW,
        initialValue = 1f,
        targetValue = 0.9f,
        durationMillis = 800
    )
    
    // 摇头动画
    val shakeRotation by rememberInfiniteFloatAnimation(
        enabled = behavior == BehaviorAnimation.SHAKE,
        initialValue = -5f,
        targetValue = 5f,
        durationMillis = 300
    )
    
    // 弹跳动画
    val bounceScale by rememberInfiniteFloatAnimation(
        enabled = behavior == BehaviorAnimation.BOUNCE,
        initialValue = 0.95f,
        targetValue = 1.05f,
        durationMillis = 500
    )
    
    Box(
        modifier = modifier
            .offset(y = jumpOffset.dp)
            .scale(
                when (behavior) {
                    BehaviorAnimation.BOW -> bowScale
                    BehaviorAnimation.BOUNCE -> bounceScale
                    else -> 1f
                }
            )
    ) {
        SimpleCitizenMarker(citizen = citizen)
    }
}

/**
 * 无限循环动画辅助函数
 */
@Composable
private fun rememberInfiniteFloatAnimation(
    enabled: Boolean,
    initialValue: Float,
    targetValue: Float,
    durationMillis: Int
): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "behavior_animation")
    
    return if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = initialValue,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animation_value"
        )
    } else {
        remember { mutableStateOf(0f) }
    }
}

/**
 * 根据市民状态计算情绪
 */
private fun calculateEmotion(citizen: Citizen): EmotionType {
    return when {
        // 非常开心（高幸福度且无抱怨）
        citizen.happiness > 0.8f && citizen.complaints == 0 -> EmotionType.VERY_HAPPY
        
        // 开心（高幸福度）
        citizen.happiness > 0.7f -> EmotionType.HAPPY
        
        // 恋爱中（已婚且幸福）
        citizen.maritalStatus == com.citysimulator.game.data.model.MaritalStatus.MARRIED && 
        citizen.happiness > 0.6f -> EmotionType.LOVE
        
        // 抱怨（有抱怨记录）
        citizen.complaints > 3 -> EmotionType.COMPLAINING
        
        // 愤怒（低幸福度且多抱怨）
        citizen.happiness < 0.3f && citizen.complaints > 1 -> EmotionType.ANGRY
        
        // 悲伤（低幸福度）
        citizen.happiness < 0.4f -> EmotionType.SAD
        
        // 疲惫（低健康度）
        citizen.health < 0.4f -> EmotionType.TIRED
        
        // 工作中（有职业）
        citizen.occupation != null -> EmotionType.WORKING
        
        // 思考中（无职业）
        citizen.occupation == null -> EmotionType.THINKING
        
        // 默认中性
        else -> EmotionType.NEUTRAL
    }
}

/**
 * 获取情绪对应的表情符号
 */
private fun getEmotionEmoji(emotion: EmotionType): String {
    return when (emotion) {
        EmotionType.HAPPY -> "😊"
        EmotionType.VERY_HAPPY -> "🎉"
        EmotionType.SAD -> "😢"
        EmotionType.ANGRY -> "😠"
        EmotionType.LOVE -> "❤️"
        EmotionType.CONFUSED -> "❓"
        EmotionType.TIRED -> "😴"
        EmotionType.WORKING -> "💼"
        EmotionType.THINKING -> "💭"
        EmotionType.CELEBRATING -> "🎊"
        EmotionType.COMPLAINING -> "💢"
        EmotionType.NEUTRAL -> "😐"
    }
}

/**
 * 根据情绪获取对应的行为动画
 */
private fun getBehaviorForEmotion(emotion: EmotionType): BehaviorAnimation {
    return when (emotion) {
        EmotionType.HAPPY, EmotionType.VERY_HAPPY -> BehaviorAnimation.BOUNCE
        EmotionType.CELEBRATING -> BehaviorAnimation.JUMP
        EmotionType.SAD, EmotionType.TIRED -> BehaviorAnimation.BOW
        EmotionType.ANGRY, EmotionType.COMPLAINING -> BehaviorAnimation.SHAKE
        EmotionType.LOVE -> BehaviorAnimation.WAVE
        else -> BehaviorAnimation.IDLE
    }
}

/**
 * 社交互动气泡（当两个市民相遇时）
 */
@Composable
fun SocialInteractionBubble(
    citizen1: Citizen,
    citizen2: Citizen,
    modifier: Modifier = Modifier
) {
    // 判断关系类型
    val interactionType = remember(citizen1, citizen2) {
        when {
            // 如果是配偶
            citizen1.familyId == citizen2.familyId && 
            citizen1.maritalStatus == com.citysimulator.game.data.model.MaritalStatus.MARRIED -> 
                "❤️❤️" // 双爱心
            
            // 如果幸福度都高
            citizen1.happiness > 0.7f && citizen2.happiness > 0.7f -> 
                "🎉" // 庆祝
            
            // 如果都在抱怨
            citizen1.complaints > 2 && citizen2.complaints > 2 -> 
                "💬💢" // 聊天+抱怨
            
            // 默认友好互动
            else -> "👋" // 挥手
        }
    }
    
    // 气泡动画
    val infiniteTransition = rememberInfiniteTransition(label = "interaction")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(32.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = interactionType,
            fontSize = 16.sp
        )
    }
}

