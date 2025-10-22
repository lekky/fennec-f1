package com.f1calendar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "races")
data class Race(
    @PrimaryKey
    @SerializedName("round")
    val round: Int,

    @SerializedName("raceName")
    val raceName: String,

    @SerializedName("Circuit")
    val circuit: Circuit,

    @SerializedName("date")
    val date: String,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("FirstPractice")
    val firstPractice: Session? = null,

    @SerializedName("SecondPractice")
    val secondPractice: Session? = null,

    @SerializedName("ThirdPractice")
    val thirdPractice: Session? = null,

    @SerializedName("Qualifying")
    val qualifying: Session? = null,

    @SerializedName("Sprint")
    val sprint: Session? = null,

    val season: String
)

data class Circuit(
    @SerializedName("circuitId")
    val circuitId: String,

    @SerializedName("circuitName")
    val circuitName: String,

    @SerializedName("Location")
    val location: Location
)

data class Location(
    @SerializedName("lat")
    val lat: String,

    @SerializedName("long")
    val long: String,

    @SerializedName("locality")
    val locality: String,

    @SerializedName("country")
    val country: String
)

data class Session(
    @SerializedName("date")
    val date: String,

    @SerializedName("time")
    val time: String? = null
)
