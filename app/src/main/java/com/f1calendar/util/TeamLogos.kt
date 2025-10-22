package com.f1calendar.util

object TeamLogos {

    /**
     * Gets the logo URL for a constructor
     * Using Formula1.com CDN logos where available
     */
    fun getLogoUrl(constructorId: String): String? {
        return logoMap[constructorId.lowercase()]
    }

    /**
     * Gets a fallback emoji for teams without logos
     */
    fun getFallbackEmoji(constructorId: String): String {
        return when (constructorId.lowercase()) {
            "red_bull" -> "🐂"
            "ferrari" -> "🐎"
            "mercedes" -> "⭐"
            "mclaren" -> "🧡"
            "aston_martin" -> "💚"
            "alpine" -> "🔵"
            "williams" -> "🔷"
            "rb" -> "🤖"
            "kick_sauber", "sauber" -> "🟢"
            "haas" -> "🇺🇸"
            else -> "🏎️"
        }
    }

    private val logoMap = mapOf(
        // 2024/2025 Teams - Using placeholder URLs that could be replaced with real CDN URLs
        "red_bull" to "https://www.formula1.com/content/dam/fom-website/teams/2024/red-bull-racing-logo.png.transform/2col/image.png",
        "ferrari" to "https://www.formula1.com/content/dam/fom-website/teams/2024/ferrari-logo.png.transform/2col/image.png",
        "mercedes" to "https://www.formula1.com/content/dam/fom-website/teams/2024/mercedes-logo.png.transform/2col/image.png",
        "mclaren" to "https://www.formula1.com/content/dam/fom-website/teams/2024/mclaren-logo.png.transform/2col/image.png",
        "aston_martin" to "https://www.formula1.com/content/dam/fom-website/teams/2024/aston-martin-logo.png.transform/2col/image.png",
        "alpine" to "https://www.formula1.com/content/dam/fom-website/teams/2024/alpine-logo.png.transform/2col/image.png",
        "williams" to "https://www.formula1.com/content/dam/fom-website/teams/2024/williams-logo.png.transform/2col/image.png",
        "rb" to "https://www.formula1.com/content/dam/fom-website/teams/2024/rb-logo.png.transform/2col/image.png",
        "kick_sauber" to "https://www.formula1.com/content/dam/fom-website/teams/2024/kick-sauber-logo.png.transform/2col/image.png",
        "sauber" to "https://www.formula1.com/content/dam/fom-website/teams/2024/kick-sauber-logo.png.transform/2col/image.png",
        "haas" to "https://www.formula1.com/content/dam/fom-website/teams/2024/haas-f1-team-logo.png.transform/2col/image.png",

        // Historical teams
        "racing_point" to "https://www.formula1.com/content/dam/fom-website/teams/2020/racing-point-logo.png.transform/2col/image.png",
        "renault" to "https://www.formula1.com/content/dam/fom-website/teams/2020/renault-logo.png.transform/2col/image.png",
        "alphatauri" to "https://www.formula1.com/content/dam/fom-website/teams/2023/alphatauri-logo.png.transform/2col/image.png",
        "alfa" to "https://www.formula1.com/content/dam/fom-website/teams/2023/alfa-romeo-logo.png.transform/2col/image.png"
    )
}
