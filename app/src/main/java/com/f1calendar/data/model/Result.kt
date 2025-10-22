package com.f1calendar.data.model

import com.google.gson.annotations.SerializedName

data class RaceResult(
    @SerializedName("number")
    val number: String,

    @SerializedName("position")
    val position: String,

    @SerializedName("positionText")
    val positionText: String,

    @SerializedName("points")
    val points: String,

    @SerializedName("Driver")
    val driver: Driver,

    @SerializedName("Constructor")
    val constructor: Constructor,

    @SerializedName("grid")
    val grid: String,

    @SerializedName("laps")
    val laps: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("Time")
    val time: Time? = null,

    @SerializedName("FastestLap")
    val fastestLap: FastestLap? = null
)

data class Time(
    @SerializedName("millis")
    val millis: String? = null,

    @SerializedName("time")
    val time: String
)

data class FastestLap(
    @SerializedName("rank")
    val rank: String,

    @SerializedName("lap")
    val lap: String,

    @SerializedName("Time")
    val time: LapTime,

    @SerializedName("AverageSpeed")
    val averageSpeed: AverageSpeed
)

data class LapTime(
    @SerializedName("time")
    val time: String
)

data class AverageSpeed(
    @SerializedName("units")
    val units: String,

    @SerializedName("speed")
    val speed: String
)

data class QualifyingResult(
    @SerializedName("number")
    val number: String,

    @SerializedName("position")
    val position: String,

    @SerializedName("Driver")
    val driver: Driver,

    @SerializedName("Constructor")
    val constructor: Constructor,

    @SerializedName("Q1")
    val q1: String? = null,

    @SerializedName("Q2")
    val q2: String? = null,

    @SerializedName("Q3")
    val q3: String? = null
)
