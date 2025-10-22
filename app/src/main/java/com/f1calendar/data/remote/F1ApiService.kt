package com.f1calendar.data.remote

import com.f1calendar.data.remote.dto.ApiResponse
import com.f1calendar.data.remote.dto.RaceTable
import com.f1calendar.data.remote.dto.StandingsTable
import retrofit2.http.GET
import retrofit2.http.Path

interface F1ApiService {

    @GET("api/f1/{season}.json")
    suspend fun getSeasonRaces(
        @Path("season") season: String = "current"
    ): ApiResponse<RaceTable>

    @GET("api/f1/{season}/{round}/results.json")
    suspend fun getRaceResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("api/f1/{season}/{round}/qualifying.json")
    suspend fun getQualifyingResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("api/f1/{season}/{round}/sprint.json")
    suspend fun getSprintResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("api/f1/{season}/driverStandings.json")
    suspend fun getDriverStandings(
        @Path("season") season: String = "current"
    ): ApiResponse<StandingsTable>

    @GET("api/f1/{season}/constructorStandings.json")
    suspend fun getConstructorStandings(
        @Path("season") season: String = "current"
    ): ApiResponse<StandingsTable>
}
