package com.f1calendar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "constructors")
data class Constructor(
    @PrimaryKey
    @SerializedName("constructorId")
    val constructorId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("nationality")
    val nationality: String
)

data class ConstructorStanding(
    @SerializedName("position")
    val position: String,

    @SerializedName("positionText")
    val positionText: String,

    @SerializedName("points")
    val points: String,

    @SerializedName("wins")
    val wins: String,

    @SerializedName("Constructor")
    val constructor: Constructor
)
