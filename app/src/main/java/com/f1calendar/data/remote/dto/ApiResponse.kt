package com.f1calendar.data.remote.dto

import com.f1calendar.data.model.*
import com.google.gson.annotations.SerializedName

data class MRData<T>(
    @SerializedName("xmlns")
    val xmlns: String,

    @SerializedName("series")
    val series: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("limit")
    val limit: String,

    @SerializedName("offset")
    val offset: String,

    @SerializedName("total")
    val total: String,

    @SerializedName("RaceTable")
    val raceTable: RaceTable? = null,

    @SerializedName("StandingsTable")
    val standingsTable: StandingsTable? = null
)

data class ApiResponse<T>(
    @SerializedName("MRData")
    val mrData: MRData<T>
)

data class RaceTable(
    @SerializedName("season")
    val season: String,

    @SerializedName("Races")
    val races: List<RaceResponse>
)

data class RaceResponse(
    @SerializedName("season")
    val season: String,

    @SerializedName("round")
    val round: String,

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

    @SerializedName("Results")
    val results: List<RaceResult>? = null,

    @SerializedName("QualifyingResults")
    val qualifyingResults: List<QualifyingResult>? = null,

    @SerializedName("SprintResults")
    val sprintResults: List<RaceResult>? = null
)

data class StandingsTable(
    @SerializedName("season")
    val season: String,

    @SerializedName("StandingsLists")
    val standingsLists: List<StandingsList>
)

data class StandingsList(
    @SerializedName("season")
    val season: String,

    @SerializedName("round")
    val round: String,

    @SerializedName("DriverStandings")
    val driverStandings: List<DriverStanding>? = null,

    @SerializedName("ConstructorStandings")
    val constructorStandings: List<ConstructorStanding>? = null
)
