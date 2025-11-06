package com.citysimulator.game.audio

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 音效扩展函数
 * 
 * 提供便捷的方式在Compose UI中触发音效
 */

/**
 * 带音效的点击修饰符
 * 
 * 使用示例:
 * ```
 * Button(
 *     modifier = Modifier.clickableWithSound(SoundType.BUTTON_CLICK) {
 *         // 点击处理逻辑
 *     }
 * )
 * ```
 */
fun Modifier.clickableWithSound(
    soundType: SoundType = SoundType.BUTTON_CLICK,
    onClick: () -> Unit
): Modifier = this.clickable {
    SoundManager.playSound(soundType)
    onClick()
}

/**
 * 播放建造成功音效
 */
fun playBuildSuccessSound() {
    SoundManager.playSound(SoundType.BUILD_SUCCESS)
}

/**
 * 播放建造失败音效
 */
fun playBuildFailSound() {
    SoundManager.playSound(SoundType.BUILD_FAIL)
}

/**
 * 播放拆除建筑音效
 */
fun playDemolishSound() {
    SoundManager.playSound(SoundType.DEMOLISH)
}

/**
 * 播放获得金币音效
 */
fun playCoinEarnSound() {
    SoundManager.playSound(SoundType.COIN_EARN)
}

/**
 * 播放消费金币音效
 */
fun playCoinSpendSound() {
    SoundManager.playSound(SoundType.COIN_SPEND)
}

/**
 * 播放警告音效
 */
fun playWarningSound() {
    SoundManager.playSound(SoundType.WARNING)
}

/**
 * 播放错误音效
 */
fun playErrorSound() {
    SoundManager.playSound(SoundType.ERROR)
}

/**
 * 播放奇观完成音效
 */
fun playWonderCompleteSound() {
    SoundManager.playSound(SoundType.WONDER_COMPLETE)
}

