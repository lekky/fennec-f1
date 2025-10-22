package com.f1calendar.util

object CountryFlags {

    /**
     * Converts a country name or nationality to its flag emoji
     */
    fun getFlag(countryOrNationality: String): String {
        val countryCode = getCountryCode(countryOrNationality)
        return countryCode?.let { countryCodeToEmoji(it) } ?: "🏁"
    }

    /**
     * Maps country names and nationalities to ISO 3166-1 alpha-2 country codes
     */
    private fun getCountryCode(name: String): String? {
        return countryMap[name.lowercase().trim()]
    }

    /**
     * Converts ISO 3166-1 alpha-2 country code to flag emoji
     * Example: "US" -> "🇺🇸"
     */
    private fun countryCodeToEmoji(countryCode: String): String {
        if (countryCode.length != 2) return "🏁"

        val firstChar = Character.codePointAt(countryCode, 0)
        val secondChar = Character.codePointAt(countryCode, 1)

        // Regional indicator symbols start at 0x1F1E6 (for 'A')
        val firstRegionalIndicator = 0x1F1E6 + (firstChar - 'A'.code)
        val secondRegionalIndicator = 0x1F1E6 + (secondChar - 'A'.code)

        return String(Character.toChars(firstRegionalIndicator)) +
               String(Character.toChars(secondRegionalIndicator))
    }

    private val countryMap = mapOf(
        // Countries (as they appear in F1 API)
        "australia" to "AU",
        "austria" to "AT",
        "azerbaijan" to "AZ",
        "bahrain" to "BH",
        "belgium" to "BE",
        "brazil" to "BR",
        "canada" to "CA",
        "china" to "CN",
        "netherlands" to "NL",
        "france" to "FR",
        "germany" to "DE",
        "hungary" to "HU",
        "italy" to "IT",
        "japan" to "JP",
        "mexico" to "MX",
        "monaco" to "MC",
        "portugal" to "PT",
        "qatar" to "QA",
        "russia" to "RU",
        "saudi arabia" to "SA",
        "singapore" to "SG",
        "spain" to "ES",
        "sweden" to "SE",
        "switzerland" to "CH",
        "turkey" to "TR",
        "uae" to "AE",
        "uk" to "GB",
        "usa" to "US",
        "united states" to "US",
        "united kingdom" to "GB",
        "united arab emirates" to "AE",

        // Nationalities (as they appear in driver data)
        "american" to "US",
        "argentine" to "AR",
        "australian" to "AU",
        "austrian" to "AT",
        "belgian" to "BE",
        "brazilian" to "BR",
        "british" to "GB",
        "canadian" to "CA",
        "chinese" to "CN",
        "colombian" to "CO",
        "danish" to "DK",
        "dutch" to "NL",
        "finnish" to "FI",
        "french" to "FR",
        "german" to "DE",
        "hungarian" to "HU",
        "indian" to "IN",
        "italian" to "IT",
        "japanese" to "JP",
        "mexican" to "MX",
        "monegasque" to "MC",
        "new zealander" to "NZ",
        "polish" to "PL",
        "russian" to "RU",
        "spanish" to "ES",
        "swedish" to "SE",
        "swiss" to "CH",
        "thai" to "TH",
        "venezuelan" to "VE"
    )
}
