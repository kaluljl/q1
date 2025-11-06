package com.citysimulator.game.data.model

/**
 * 城市区域枚举
 * 
 * 将城市划分为不同的功能区域，每个区域有独立的地图和建筑
 */
enum class CityDistrict(
    val displayName: String,
    val icon: String,
    val description: String
) {
    /**
     * 主城区 - 商业和住宅中心
     */
    DOWNTOWN(
        displayName = "主城区",
        icon = "🏙️",
        description = "商业繁华的市中心"
    ),
    
    /**
     * 工业区 - 重工业和生产中心
     */
    INDUSTRIAL(
        displayName = "工业区",
        icon = "🏭",
        description = "工业生产区域"
    ),
    
    /**
     * 农业区 - 粮食生产区域
     */
    AGRICULTURAL(
        displayName = "农业区",
        icon = "🌾",
        description = "农业种植区域"
    ),
    
    /**
     * 郊区 - 住宅和休闲区
     */
    SUBURBAN(
        displayName = "郊区",
        icon = "🏡",
        description = "宁静的居住区"
    );
    
    /**
     * 获取区域的主题颜色
     */
    fun getThemeColor(): androidx.compose.ui.graphics.Color {
        return when (this) {
            DOWNTOWN -> androidx.compose.ui.graphics.Color(0xFF2196F3) // 蓝色 - 现代都市
            INDUSTRIAL -> androidx.compose.ui.graphics.Color(0xFFFF9800) // 橙色 - 工业警示
            AGRICULTURAL -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // 绿色 - 自然农业
            SUBURBAN -> androidx.compose.ui.graphics.Color(0xFF9C27B0) // 紫色 - 宁静郊区
        }
    }
}

/**
 * 区域数据
 * 
 * 存储某个区域的所有建筑和状态
 */
data class DistrictData(
    val district: CityDistrict,
    val buildings: MutableList<Building> = mutableListOf(),
    val unlocked: Boolean = true, // 是否已解锁（未来可以做解锁系统）
    val pollution: Float = 0f, // 污染值 (0.0-1.0)
    val prosperity: Float = 0.5f // 繁荣度 (0.0-1.0)
)

