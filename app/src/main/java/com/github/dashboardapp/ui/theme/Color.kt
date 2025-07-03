package com.github.dashboardapp.ui.theme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
val MatteBlack = Color(0xFF1C1C1E)
val CardBackground = Color(0xFF2C2C2E)
val TextPrimary = Color.White
val TextSecondary = Color(0xFF8E8E93)
val IconTint = Color(0xFFAAAAAA)
val SkyPulse = Brush.horizontalGradient(listOf(Color(0xFF00A4CC), Color(0xFF2A78F4)))
val SolarBloom = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFB87333)))
val RoyalFlow = Brush.horizontalGradient(listOf(Color(0xFF6A0DAD), Color(0xFFE6E6FA)))
val MagmaCore = Brush.horizontalGradient(listOf(Color(0xFFE34234), Color(0xFFFFA500)))
val AuroraRise = Brush.horizontalGradient(listOf(Color(0xFF008080), Color(0xFFFFC0CB)))
val ArcticMist = Brush.horizontalGradient(listOf(Color(0xFFADD8E6), Color(0xFFFFFFFF)))
val InfernoEdge = Brush.horizontalGradient(listOf(Color(0xFFDC143C), Color(0xFFFF4500)))
val ForestDrift = Brush.horizontalGradient(listOf(Color(0xFF808000), Color(0xFF556B2F)))
val NightCircuit = Brush.horizontalGradient(listOf(Color(0xFF4B0082), Color(0xFF0000FF)))
val RoseFade = Brush.horizontalGradient(listOf(Color(0xFFFFC0CB), Color(0xFFFF7F50)))
fun getGradientById(id: Int?): Brush? {
    return when (id) {
        1 -> SkyPulse; 2 -> SolarBloom; 3 -> RoyalFlow; 4 -> MagmaCore; 5 -> AuroraRise
        6 -> ArcticMist; 7 -> InfernoEdge; 8 -> ForestDrift; 9 -> NightCircuit; 10 -> RoseFade
        else -> null
    }
}
