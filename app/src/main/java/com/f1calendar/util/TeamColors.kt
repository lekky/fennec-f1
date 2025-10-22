package com.f1calendar.util

import androidx.compose.ui.graphics.Color

object TeamColors {
    private val teamColorMap = mapOf(
        "red_bull" to Color(0xFF1E41FF),
        "ferrari" to Color(0xFFE8002D),
        "mercedes" to Color(0xFF00D2BE),
        "mclaren" to Color(0xFFFF8700),
        "aston_martin" to Color(0xFF006F62),
        "alpine" to Color(0xFF0090FF),
        "williams" to Color(0xFF005AFF),
        "alphatauri" to Color(0xFF2B4562),
        "alfa" to Color(0xFF900000),
        "haas" to Color(0xFFFFFFFF),
        "racing_bulls" to Color(0xFF2B4562),
        "kick_sauber" to Color(0xFF00E700),
        "rb" to Color(0xFF1E41FF),
        "sauber" to Color(0xFF00E700)
    )

    fun getTeamColor(constructorId: String): Color {
        return teamColorMap[constructorId.lowercase()] ?: Color(0xFF15151E)
    }

    fun getTeamColorFromName(constructorName: String): Color {
        return when {
            constructorName.contains("Red Bull", ignoreCase = true) -> Color(0xFF1E41FF)
            constructorName.contains("Ferrari", ignoreCase = true) -> Color(0xFFE8002D)
            constructorName.contains("Mercedes", ignoreCase = true) -> Color(0xFF00D2BE)
            constructorName.contains("McLaren", ignoreCase = true) -> Color(0xFFFF8700)
            constructorName.contains("Aston Martin", ignoreCase = true) -> Color(0xFF006F62)
            constructorName.contains("Alpine", ignoreCase = true) -> Color(0xFF0090FF)
            constructorName.contains("Williams", ignoreCase = true) -> Color(0xFF005AFF)
            constructorName.contains("AlphaTauri", ignoreCase = true) -> Color(0xFF2B4562)
            constructorName.contains("Racing Bulls", ignoreCase = true) -> Color(0xFF2B4562)
            constructorName.contains("RB", ignoreCase = true) -> Color(0xFF2B4562)
            constructorName.contains("Alfa Romeo", ignoreCase = true) -> Color(0xFF900000)
            constructorName.contains("Haas", ignoreCase = true) -> Color(0xFFB6BABD)
            constructorName.contains("Sauber", ignoreCase = true) -> Color(0xFF00E700)
            constructorName.contains("Kick Sauber", ignoreCase = true) -> Color(0xFF00E700)
            else -> Color(0xFF15151E)
        }
    }
}
