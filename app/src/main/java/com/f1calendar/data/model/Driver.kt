package com.f1calendar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey
    @SerializedName("driverId")
    val driverId: String,

    @SerializedName("permanentNumber")
    val permanentNumber: String? = null,

    @SerializedName("code")
    val code: String,

    @SerializedName("givenName")
    val givenName: String,

    @SerializedName("familyName")
    val familyName: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String,

    @SerializedName("nationality")
    val nationality: String
)

data class DriverStanding(
    @SerializedName("position")
    val position: String,

    @SerializedName("positionText")
    val positionText: String,

    @SerializedName("points")
    val points: String,

    @SerializedName("wins")
    val wins: String,

    @SerializedName("Driver")
    val driver: Driver,

    @SerializedName("Constructors")
    val constructors: List<Constructor>
)
