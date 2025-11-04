package com.citysimulator.game.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

/**
 * 游戏主题系统
 * 
 * 支持多种视觉风格：科幻、魔法、田园、赛博朋克、复古
 */

/**
 * 主题类型枚举
 */
enum class ThemeType {
    DEFAULT,        // 默认主题
    SCI_FI,        // 科幻主题
    MAGIC,         // 魔法主题
    PASTORAL,      // 田园主题
    CYBERPUNK,     // 赛博朋克主题
    RETRO          // 复古主题
}

/**
 * 主题配色方案
 */
data class GameThemeColors(
    val themeType: ThemeType,
    val themeName: String,
    val themeIcon: String,
    val themeDescription: String,
    
    // 背景色
    val background: Color,
    val backgroundGradient: List<Color>,
    
    // 主色调
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    
    // 建筑颜色（住宅、商业、工业、公共）
    val residentialColor: Color,
    val commercialColor: Color,
    val industrialColor: Color,
    val publicColor: Color,
    
    // UI元素颜色
    val cardBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    
    // 状态颜色
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
    
    // 特殊效果
    val glowColor: Color,
    val accentColor: Color
)

/**
 * 获取主题配色
 */
fun getThemeColors(themeType: ThemeType): GameThemeColors {
    return when (themeType) {
        ThemeType.DEFAULT -> defaultTheme
        ThemeType.SCI_FI -> sciFiTheme
        ThemeType.MAGIC -> magicTheme
        ThemeType.PASTORAL -> pastoralTheme
        ThemeType.CYBERPUNK -> cyberpunkTheme
        ThemeType.RETRO -> retroTheme
    }
}

/**
 * 默认主题 - 现代清新
 */
private val defaultTheme = GameThemeColors(
    themeType = ThemeType.DEFAULT,
    themeName = "现代风格",
    themeIcon = "🏙️",
    themeDescription = "简约现代的城市风格",
    
    background = Color(0xFF252537),  // 调亮背景
    backgroundGradient = listOf(
        Color(0xFF252537),
        Color(0xFF2A2A45),
        Color(0xFF1F3A5F)
    ),
    
    primary = Color(0xFF2196F3),
    primaryVariant = Color(0xFF1976D2),
    secondary = Color(0xFF03DAC6),
    secondaryVariant = Color(0xFF018786),
    
    residentialColor = Color(0xFFFF8A80),
    commercialColor = Color(0xFFFFD54F),
    industrialColor = Color(0xFF90A4AE),
    publicColor = Color(0xFF81C784),
    
    cardBackground = Color(0xFF3D3D52),  // 大幅调亮卡片（提升50%）
    textPrimary = Color(0xFFFFFFFF),  // 纯白
    textSecondary = Color(0xFFEEEEEE),  // 接近纯白（93%）
    divider = Color(0xFF555555),  // 调亮分割线
    
    success = Color(0xFF4CAF50),
    warning = Color(0xFFFFC107),
    error = Color(0xFFF44336),
    info = Color(0xFF2196F3),
    
    glowColor = Color(0xFF03DAC6),
    accentColor = Color(0xFFFF4081)
)

/**
 * 科幻主题 - 未来感
 */
private val sciFiTheme = GameThemeColors(
    themeType = ThemeType.SCI_FI,
    themeName = "科幻未来",
    themeIcon = "🚀",
    themeDescription = "充满未来科技感的太空城市",
    
    background = Color(0xFF151830),  // 调亮
    backgroundGradient = listOf(
        Color(0xFF151830),
        Color(0xFF253050),
        Color(0xFF354A70)
    ),
    
    primary = Color(0xFF00D9FF),
    primaryVariant = Color(0xFF00B8D4),
    secondary = Color(0xFF7C4DFF),
    secondaryVariant = Color(0xFF651FFF),
    
    residentialColor = Color(0xFF00E5FF),
    commercialColor = Color(0xFF76FF03),
    industrialColor = Color(0xFF536DFE),
    publicColor = Color(0xFFE040FB),
    
    cardBackground = Color(0xFF2A3545),  // 大幅调亮
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFE0F7FA),  // 更亮
    divider = Color(0xFF4A5A6F),
    
    success = Color(0xFF00E676),
    warning = Color(0xFFFFEA00),
    error = Color(0xFFFF1744),
    info = Color(0xFF00E5FF),
    
    glowColor = Color(0xFF00D9FF),
    accentColor = Color(0xFF7C4DFF)
)

/**
 * 魔法主题 - 奇幻魔法
 */
private val magicTheme = GameThemeColors(
    themeType = ThemeType.MAGIC,
    themeName = "魔法奇幻",
    themeIcon = "🔮",
    themeDescription = "充满魔法与奇幻的神秘城市",
    
    background = Color(0xFF251840),  // 调亮
    backgroundGradient = listOf(
        Color(0xFF251840),
        Color(0xFF382555),
        Color(0xFF553375)
    ),
    
    primary = Color(0xFFAB47BC),
    primaryVariant = Color(0xFF8E24AA),
    secondary = Color(0xFFFFD700),
    secondaryVariant = Color(0xFFFFC107),
    
    residentialColor = Color(0xFFBA68C8),
    commercialColor = Color(0xFFFFD54F),
    industrialColor = Color(0xFF7986CB),
    publicColor = Color(0xFF4DB6AC),
    
    cardBackground = Color(0xFF3D2A52),  // 大幅调亮
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFF3E5F5),  // 更亮
    divider = Color(0xFF6A2AA0),
    
    success = Color(0xFF66BB6A),
    warning = Color(0xFFFFCA28),
    error = Color(0xFFEF5350),
    info = Color(0xFFAB47BC),
    
    glowColor = Color(0xFFE1BEE7),
    accentColor = Color(0xFFFFD700)
)

/**
 * 田园主题 - 自然清新
 */
private val pastoralTheme = GameThemeColors(
    themeType = ThemeType.PASTORAL,
    themeName = "田园牧歌",
    themeIcon = "🌾",
    themeDescription = "回归自然的宁静田园小镇",
    
    background = Color(0xFF3A6B48),  // 调亮
    backgroundGradient = listOf(
        Color(0xFF3A6B48),
        Color(0xFF558B68),
        Color(0xFF75AB82)
    ),
    
    primary = Color(0xFF8BC34A),
    primaryVariant = Color(0xFF689F38),
    secondary = Color(0xFFFFB74D),
    secondaryVariant = Color(0xFFFF9800),
    
    residentialColor = Color(0xFFD4A574),
    commercialColor = Color(0xFFFFCC80),
    industrialColor = Color(0xFF90A4AE),
    publicColor = Color(0xFF81C784),
    
    cardBackground = Color(0xFF507A5C),  // 大幅调亮
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFF8FFF5),  // 更亮
    divider = Color(0xFF6A9A45),
    
    success = Color(0xFF66BB6A),
    warning = Color(0xFFFFB74D),
    error = Color(0xFFE57373),
    info = Color(0xFF4FC3F7),
    
    glowColor = Color(0xFFAED581),
    accentColor = Color(0xFFFFAB40)
)

/**
 * 赛博朋克主题 - 霓虹都市
 */
private val cyberpunkTheme = GameThemeColors(
    themeType = ThemeType.CYBERPUNK,
    themeName = "赛博朋克",
    themeIcon = "🌃",
    themeDescription = "霓虹闪烁的未来赛博都市",
    
    background = Color(0xFF1A0A35),  // 调亮
    backgroundGradient = listOf(
        Color(0xFF1A0A35),
        Color(0xFF2A1540),
        Color(0xFF380A58)
    ),
    
    primary = Color(0xFFFF006E),
    primaryVariant = Color(0xFFD6007A),
    secondary = Color(0xFF00F5FF),
    secondaryVariant = Color(0xFF00D9F5),
    
    residentialColor = Color(0xFFFF006E),
    commercialColor = Color(0xFFFB5607),
    industrialColor = Color(0xFF8338EC),
    publicColor = Color(0xFF00F5FF),
    
    cardBackground = Color(0xFF321550),  // 大幅调亮
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFFFD6F0),  // 更亮
    divider = Color(0xFF6A20C0),
    
    success = Color(0xFF00F5FF),
    warning = Color(0xFFFB5607),
    error = Color(0xFFFF006E),
    info = Color(0xFF8338EC),
    
    glowColor = Color(0xFF00F5FF),
    accentColor = Color(0xFFFF006E)
)

/**
 * 复古主题 - 怀旧温馨
 */
private val retroTheme = GameThemeColors(
    themeType = ThemeType.RETRO,
    themeName = "怀旧复古",
    themeIcon = "📻",
    themeDescription = "温馨怀旧的复古小城",
    
    background = Color(0xFF4E3830),  // 调亮
    backgroundGradient = listOf(
        Color(0xFF4E3830),
        Color(0xFF5E483E),
        Color(0xFF6E5548)
    ),
    
    primary = Color(0xFFD4A574),
    primaryVariant = Color(0xFFBCAAA4),
    secondary = Color(0xFFFFAB91),
    secondaryVariant = Color(0xFFFF8A65),
    
    residentialColor = Color(0xFFBCAAA4),
    commercialColor = Color(0xFFFFCC80),
    industrialColor = Color(0xFF90A4AE),
    publicColor = Color(0xFFA1887F),
    
    cardBackground = Color(0xFF65483E),  // 大幅调亮
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFFFF5E0),  // 更亮
    divider = Color(0xFF8A6055),
    
    success = Color(0xFF81C784),
    warning = Color(0xFFFFB74D),
    error = Color(0xFFE57373),
    info = Color(0xFF64B5F6),
    
    glowColor = Color(0xFFFFE0B2),
    accentColor = Color(0xFFFFAB91)
)

/**
 * 获取主题背景渐变
 */
fun getThemeBackgroundBrush(themeColors: GameThemeColors): Brush {
    return Brush.verticalGradient(themeColors.backgroundGradient)
}

