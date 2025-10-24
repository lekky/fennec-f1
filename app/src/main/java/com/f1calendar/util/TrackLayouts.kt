package com.f1calendar.util

object TrackLayouts {

    /**
     * Gets the track layout image URL for a circuit
     * Returns null if no track layout is available
     */
    fun getTrackLayoutUrl(circuitId: String): String? {
        return trackLayoutMap[circuitId.lowercase()]
    }

    private val trackLayoutMap = mapOf(
        // Using Wikipedia track layout images (SVG format works well)
        "monaco" to "https://upload.wikimedia.org/wikipedia/commons/3/36/Circuit_Monaco.svg",
        "silverstone" to "https://upload.wikimedia.org/wikipedia/commons/c/c9/Silverstone_Circuit_2020.svg",
        "spa" to "https://upload.wikimedia.org/wikipedia/commons/5/54/Spa-Francorchamps_of_Belgium.svg",
        "monza" to "https://upload.wikimedia.org/wikipedia/commons/f/f8/Monza_track_map.svg",
        "suzuka" to "https://upload.wikimedia.org/wikipedia/commons/e/ec/Suzuka_circuit_map--2003.svg",
        "interlagos" to "https://upload.wikimedia.org/wikipedia/commons/4/45/Aut%C3%B3dromo_Jos%C3%A9_Carlos_Pace_%28AKA_Interlagos%29.svg",
        "bahrain" to "https://upload.wikimedia.org/wikipedia/commons/2/29/Bahrain_International_Circuit--Grand_Prix_Layout.svg",
        "jeddah" to "https://upload.wikimedia.org/wikipedia/commons/8/89/Jeddah_Street_Circuit.svg",
        "albert_park" to "https://upload.wikimedia.org/wikipedia/commons/5/57/Albert_Park_Street_Circuit.svg",
        "miami" to "https://upload.wikimedia.org/wikipedia/commons/e/ed/Miami_International_Autodrome_track_map.svg",
        "imola" to "https://upload.wikimedia.org/wikipedia/commons/3/30/Imola.svg",
        "red_bull_ring" to "https://upload.wikimedia.org/wikipedia/commons/b/b2/Circuit_Red_Bull_Ring.svg",
        "hungaroring" to "https://upload.wikimedia.org/wikipedia/commons/9/91/Hungaroring.svg",
        "zandvoort" to "https://upload.wikimedia.org/wikipedia/commons/9/9f/Zandvoort_Racing_Circuit.svg",
        "monza" to "https://upload.wikimedia.org/wikipedia/commons/f/f8/Monza_track_map.svg",
        "marina_bay" to "https://upload.wikimedia.org/wikipedia/commons/1/13/Marina_Bay_Street_Circuit_track_map.svg",
        "americas" to "https://upload.wikimedia.org/wikipedia/commons/a/a5/Circuit_of_the_Americas.svg",
        "rodriguez" to "https://upload.wikimedia.org/wikipedia/commons/3/36/Aut%C3%B3dromo_Hermanos_Rodr%C3%ADguez_2015.svg",
        "yas_marina" to "https://upload.wikimedia.org/wikipedia/commons/c/c9/Yas_Marina_Circuit--Grand_Prix_Layout.svg",
        "losail" to "https://upload.wikimedia.org/wikipedia/commons/c/c5/Losail.svg",
        "vegas" to "https://upload.wikimedia.org/wikipedia/commons/c/c4/Las_Vegas_Grand_Prix_Circuit_Map.svg",
        "villeneuve" to "https://upload.wikimedia.org/wikipedia/commons/c/ca/Circuit_Gilles_Villeneuve.svg",
        "shanghai" to "https://upload.wikimedia.org/wikipedia/commons/1/10/Shanghai_International_Circuit_track_map.svg",
        "baku" to "https://upload.wikimedia.org/wikipedia/commons/6/60/Baku_Formula_One_circuit_map.svg"
    )
}
